package com.sing4u.kr.customSongRequest.service;


import com.sing4u.kr.common.exception.ApiException;
import com.sing4u.kr.common.exception.ExceptionCode;
import com.sing4u.kr.customSongRequest.dto.request.SongRequestCreateDto;
import com.sing4u.kr.customSongRequest.dto.SongRequestResponseDto;
import com.sing4u.kr.customSongRequest.dto.response.SongDetailDto;
import com.sing4u.kr.customSongRequest.entity.SongRequest;
import com.sing4u.kr.customSongRequest.repository.SongRequestRepository;
import com.sing4u.kr.session.entity.Session;
import com.sing4u.kr.session.enums.SessionStatus;
import com.sing4u.kr.session.repository.SessionRepository;
import com.sing4u.kr.session.dto.SessionSongsDto;
import com.sing4u.kr.user.repository.UserRepository;
import com.sing4u.kr.user.entity.enums.UserType; // UserType enum 경로 확인

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SongRequestService {

    private static final Logger logger = LoggerFactory.getLogger(SongRequestService.class);

    private final SongRequestRepository songRequestRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;


    @Transactional
    public SongRequestResponseDto createSongRequest(SongRequestCreateDto createDto) {
        Session session = sessionRepository.findById(createDto.getSessionId())
                .orElseThrow(() -> new ApiException(ExceptionCode.NOT_FOUND,"세션을 찾을 수 없습니다. ID: " + createDto.getSessionId()));

        // 세션 유효성 검사
        if (!session.getArtist().getId().equals(createDto.getArtistId())) {
            throw new ApiException(ExceptionCode.CONFLICT, "세션이 지정된 아티스트에게 속하지 않습니다.");
        }
        if (session.getStatus() != SessionStatus.OPEN) {
            throw new ApiException(ExceptionCode.CONFLICT, "이 세션은 현재 신청곡을 받고 있지 않습니다.");
        }

        SongRequest songRequest = SongRequest.fromCreateDto(session, createDto);

        SongRequest savedSongRequest = songRequestRepository.save(songRequest);
        logger.info("신청곡 등록 완료: ID {}, 제목: {}", savedSongRequest.getId(), savedSongRequest.getSongTitle());

        return new SongRequestResponseDto(savedSongRequest.getId(), "신청곡 등록 완료");
    }

    @Transactional(readOnly = true)
    public List<SessionSongsDto> getSongRequestsByArtist(Long artistId) {
        userRepository.findByIdAndUserType(artistId, UserType.ARTIST)
                .orElseThrow(() -> new ApiException(ExceptionCode.NOT_FOUND,"아티스트를 찾을 수 없습니다. ID: " + artistId));

        List<Session> sessions = sessionRepository.findAllWithSongsByArtist(artistId);

        return sessions.stream()
                .map(session -> {
                    List<SongRequest> requestsForThisSession = songRequestRepository.findBySessionIdOrderByRequestedAtAsc(session.getId());

                    List<SongDetailDto> songDetails = requestsForThisSession.stream()
                            .map(SongDetailDto::from)
                            .collect(Collectors.toList());

                    return SessionSongsDto.from(session, songDetails);
                })
                .collect(Collectors.toList());
    }
}