package com.sing4u.kr.songRequest.like.service;

import com.sing4u.kr.common.exception.ApiException;
import com.sing4u.kr.common.exception.Exception400;
import com.sing4u.kr.common.exception.ExceptionCode;
import com.sing4u.kr.songRequest.entity.SongRequest;
import com.sing4u.kr.songRequest.like.dto.response.SongRequestLikeResponse;
import com.sing4u.kr.songRequest.like.entity.SongRequestLike;
import com.sing4u.kr.songRequest.like.repository.SongRequestLikeRepository;
import com.sing4u.kr.songRequest.repository.SongRequestRepository;
import com.sing4u.kr.user.entity.User;
import com.sing4u.kr.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SongRequestLikeService {
    private final SongRequestRepository songRequestRepository;
    private final SongRequestLikeRepository likeRepository;
    private final UserRepository userRepository;

    private SongRequest getRequestOrThrow(Long requestId) {
        return songRequestRepository.findById(requestId)
                .orElseThrow(() -> new ApiException(ExceptionCode.NOT_FOUND, "신청곡을 찾을 수 없습니다."));
    }

    @Transactional
    public SongRequestLikeResponse like(Long requestId, Long userId) {
        SongRequest req = getRequestOrThrow(requestId);

        if (likeRepository.existsByUserIdAndSongRequestId(userId, requestId)) {
            // 이미 눌렀다면 그대로 반환
            long count = currentCount(req);
            return new SongRequestLikeResponse(true, count);
        }

        try {
            // User 프록시만 생성해서 넣기
            User userRef = userRepository.getReferenceById(userId);

            likeRepository.save(
                    SongRequestLike.builder()
                            .user(userRef)
                            .songRequest(req)
                            .build()
            );
        } catch (DataIntegrityViolationException e) {
            // UNIQUE 제약 위반일 때만 무시 (다른 위반이면 다시 던짐)
            if (e.getMessage() != null && e.getMessage().contains("song_request_like_unique")) {
                log.debug("Duplicate like ignored (UNIQUE constraint). userId={}, requestId={}", userId, requestId);
            } else {
                throw e; // 예상 외 제약 위반은 그대로 예외로 던짐
            }
        }

        req.increaseLikeCount();

        return new SongRequestLikeResponse(true, currentCount(req));
    }

    @Transactional
    public SongRequestLikeResponse unlike(Long requestId, Long userId) {
        SongRequest req = getRequestOrThrow(requestId);

        likeRepository.findByUserIdAndSongRequestId(userId, requestId)
                .ifPresent(like -> {
                    likeRepository.delete(like);
                    req.decreaseLikeCount(); // 눌려 있었을 때만 감소
                });

        // 눌려 있지 않아도 예외 없이 성공 처리, 현재 상태만 반환
        return new SongRequestLikeResponse(false, currentCount(req));
    }

    @Transactional
    public SongRequestLikeResponse getStatus(Long requestId, Long userId) {
        SongRequest req = getRequestOrThrow(requestId);
        boolean liked = likeRepository.existsByUserIdAndSongRequestId(userId, requestId);
        long count = currentCount(req);
        return new SongRequestLikeResponse(liked, count);
    }

    private long currentCount(SongRequest req) {
        return req.getLikeCount();
    }
}
