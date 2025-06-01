package com.sing4u.kr.session.service;

import com.sing4u.kr.common.exception.ApiException;
import com.sing4u.kr.common.exception.ExceptionCode;
import com.sing4u.kr.session.dto.response.CurrentSessionResponseDto;
import com.sing4u.kr.session.dto.response.SessionResponseDto;
import com.sing4u.kr.session.entity.Session;
import com.sing4u.kr.session.enums.SessionStatus;
import com.sing4u.kr.session.repository.SessionRepository;
import com.sing4u.kr.user.entity.User;
import com.sing4u.kr.user.entity.enums.UserType;
import com.sing4u.kr.user.repository.UserRepository;
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

        return SessionResponseDto.from(savedSession);
    }

    @Transactional
    public SessionResponseDto closeSession(Long artistId, Long sessionId) {
        userRepository.findByIdAndUserType(artistId, UserType.ARTIST)
                .orElseThrow(() -> new ApiException(ExceptionCode.NOT_FOUND, "아티스트를 찾을 수 없습니다. ID: " + artistId));

        Session session = sessionRepository.findByIdAndArtistId(sessionId, artistId)
                .orElseThrow(() -> new ApiException(ExceptionCode.NOT_FOUND, "해당 아티스트의 세션을 찾을 수 없습니다. Session ID: " + sessionId));

        session.close();

        return SessionResponseDto.from(session);
    }

    public CurrentSessionResponseDto getArtistOpenSession(Long artistId) {
        userRepository.findByIdAndUserType(artistId, UserType.ARTIST)
                .orElseThrow(() -> new ApiException(ExceptionCode.NOT_FOUND, "아티스트를 찾을 수 없습니다. ID: " + artistId));

        return sessionRepository.findByArtistIdAndStatus(artistId, SessionStatus.OPEN)
                .map(CurrentSessionResponseDto::from)
                .orElse(null);
    }

}
