package com.sing4u.kr.session.service;

import com.sing4u.kr.application.utils.SecurityContextUtils;
import com.sing4u.kr.common.exception.ApiException;
import com.sing4u.kr.common.exception.ExceptionCode;
import com.sing4u.kr.session.dto.response.CurrentSessionResponseDto;
import com.sing4u.kr.session.dto.response.SessionResponseDto;
import com.sing4u.kr.session.entity.Session;
import com.sing4u.kr.session.enums.SessionStatus;
import com.sing4u.kr.session.repository.SessionRepository;
import com.sing4u.kr.songRequest.dto.SongRequestResponseDto;
import com.sing4u.kr.songRequest.dto.response.SongDetailDto;
import com.sing4u.kr.songRequest.repository.SongRequestRepository;
import com.sing4u.kr.user.entity.User;
import com.sing4u.kr.user.entity.enums.UserType;
import com.sing4u.kr.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionService {
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final SongRequestRepository songRequestRepository;

    @Transactional
    public SessionResponseDto createSession(Long artistId){
        User artist = userRepository.findByIdAndUserTypeAndDeletedAtIsNull(artistId, UserType.ARTIST)
                .orElseThrow(() -> new ApiException(ExceptionCode.NOT_FOUND, "해당 아티스트가 존재하지 않습니다."));

        // 이미 열린 세션이 있는지 확인
        Optional<Session> existingOpenSession = sessionRepository.findByArtistAndStatus(artist, SessionStatus.OPEN);
        if (existingOpenSession.isPresent()) {
            // 또는 기존 세션 정보를 반환하거나 예외처리
            throw new ApiException(ExceptionCode.CONFLICT, "이미 진행 중인 신청곡 받기 세션이 있습니다. Session ID: " + existingOpenSession.get().getId());

            /* return SessionResponseDto.builder() // 예시: 기존 세션 정보 반환
                    .sessionId(existingOpenSession.get().getId())
                    .artistId(existingOpenSession.get().getArtist().getId())
                    .status(existingOpenSession.get().getStatus())
                    .startedAt(existingOpenSession.get().getStartedAt())
                    .build();
             */
        }

        Session newSession = Session.create(artist);
        Session savedSession = sessionRepository.save(newSession);

        // 아티스트 상태도 함께 오픈 처리
        artist.updateIsOpen(true);

        return SessionResponseDto.from(savedSession);
    }

    @Transactional
    public SessionResponseDto closeSession(Long artistId, Long sessionId) {
        User artist = userRepository.findByIdAndUserTypeAndDeletedAtIsNull(artistId, UserType.ARTIST)
                .orElseThrow(() -> new ApiException(ExceptionCode.NOT_FOUND, "아티스트를 찾을 수 없습니다. ID: " + artistId));

        Session session = sessionRepository.findByIdAndArtistId(sessionId, artistId)
                .orElseThrow(() -> new ApiException(ExceptionCode.NOT_FOUND, "해당 아티스트의 세션을 찾을 수 없습니다. Session ID: " + sessionId));

        session.close();

        // 아티스트 상태도 함께 처리
        artist.updateIsOpen(false);

        return SessionResponseDto.from(session);
    }

//    public CurrentSessionResponseDto getArtistOpenSession(Long artistId, Boolean sessionOpenClose) {
//        userRepository.findByIdAndUserTypeAndDeletedAtIsNull(artistId, UserType.ARTIST)
//                .orElseThrow(() -> new ApiException(ExceptionCode.NOT_FOUND, "아티스트를 찾을 수 없습니다. ID: " + artistId));
//
//        SessionStatus status = Boolean.TRUE.equals(sessionOpenClose) ? SessionStatus.OPEN : SessionStatus.CLOSE;
//
//        return sessionRepository.findByArtistIdAndStatus(artistId, status)
//                .map(CurrentSessionResponseDto::from)
//                .orElse(null);
//    }

    public CurrentSessionResponseDto getArtistSessionStatus(Long artistId) {
        // 아티스트 존재 확인
        userRepository.findByIdAndUserTypeAndDeletedAtIsNull(artistId, UserType.ARTIST)
                .orElseThrow(() -> new ApiException(ExceptionCode.NOT_FOUND, "아티스트를 찾을 수 없습니다. ID: " + artistId));

        // 세션 상태 조회 (OPEN이든 CLOSE든 가장 최신 세션 가져오기)
        return sessionRepository.findTopByArtistIdOrderByStartedAtDesc(artistId)
                .map(CurrentSessionResponseDto::from)
                .orElse(null);
    }

    public Page<Session> getSessionsByArtist(Long artistId, Pageable pageable) {
        // 아티스트 존재 확인
        userRepository.findByIdAndUserTypeAndDeletedAtIsNull(artistId, UserType.ARTIST)
                .orElseThrow(() -> new ApiException(ExceptionCode.NOT_FOUND, "아티스트를 찾을 수 없습니다. ID: " + artistId));

        return sessionRepository.findByArtistIdOrderByStartedAtDesc(artistId, pageable);
    }
  
    @Transactional
    public void closeAllOpenByArtist(Long artistId) {
        sessionRepository.findAllByArtistIdAndStatus(artistId, SessionStatus.OPEN)
                .forEach(Session::close);
        userRepository.findByIdAndUserTypeAndDeletedAtIsNull(artistId, UserType.ARTIST)
                .ifPresent(artist -> artist.updateIsOpen(false));
    }

    @Transactional(readOnly = true)
    public Page<SongDetailDto> getRecommendHistorySessions(
            String artistPublicId,
            Pageable pageable
    ) {
        // 1. 아티스트 ID 조회 (존재 검증 포함)
        User artist = userRepository.findByUserPublicIdAndDeletedAtIsNull(artistPublicId)
                .orElseThrow(() -> new ApiException(
                        ExceptionCode.NOT_FOUND,
                        "아티스트를 찾을 수 없습니다."
                ));

        // 2. 현재 로그인한 사용자 이메일 찾기
        String email = SecurityContextUtils.getEmail();


        // 3. 추천 히스토리 세션 조회
        return songRequestRepository
                .findRecommendHistorySongRequests(
                        artist.getId(),
                        email,
                        pageable
                );
    }

}
