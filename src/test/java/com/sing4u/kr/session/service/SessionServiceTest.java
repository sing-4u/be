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
import com.sing4u.kr.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionService 테스트")
public class SessionServiceTest {

    @InjectMocks
    private SessionService sessionService;

    @Mock
    private UserService userService;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    private final String artistPublicId = "test-public-id";
    private final Long sessionId = 10L;
    private final Long artistId = 1L;
    private User artist;
    private Session savedSession;
    private Session openSession;
    private Session closedSession;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        artist = User.testUserBuilder(artistId, artistPublicId, "testArtist", UserType.ARTIST);
        savedSession = Session.builder().id(sessionId).artist(artist).status(SessionStatus.OPEN).startedAt(now).build();
        openSession = Session.builder().id(sessionId).artist(artist).status(SessionStatus.OPEN).startedAt(now).build();
        closedSession = Session.builder().id(sessionId).artist(artist).status(SessionStatus.CLOSE).startedAt(now).closedAt(now.plusHours(1)).build();
    }

    @Test
    @DisplayName("세션 생성 성공")
    void createSession_success() {
        when(userService.getUserIdByPublicId(artistPublicId)).thenReturn(artistId);
        when(userRepository.findByIdAndUserTypeAndDeletedAtIsNull(artistId, UserType.ARTIST)).thenReturn(Optional.of(artist));
        when(sessionRepository.findByArtistAndStatus(artist, SessionStatus.OPEN)).thenReturn(Optional.empty());
        when(sessionRepository.save(any(Session.class))).thenReturn(savedSession);

        Long resolvedArtistId = userService.getUserIdByPublicId(artistPublicId);
        SessionResponseDto responseDto = sessionService.createSession(resolvedArtistId);

        assertNotNull(responseDto);
        assertEquals(sessionId, responseDto.getSessionId());
        assertEquals(artistPublicId, responseDto.getArtistId());
        assertEquals(SessionStatus.OPEN, responseDto.getStatus());
        assertNotNull(responseDto.getStartedAt());

        verify(userRepository, times(1)).findByIdAndUserTypeAndDeletedAtIsNull(resolvedArtistId, UserType.ARTIST);
        verify(sessionRepository, times(1)).findByArtistAndStatus(artist, SessionStatus.OPEN);
        verify(sessionRepository, times(1)).save(any(Session.class));
    }

    @Test
    @DisplayName("세션 생성 실패 - 아티스트를 찾을 수 없음")
    void createSession_artistNotFound() {
        when(userService.getUserIdByPublicId(artistPublicId)).thenReturn(artistId);
        when(userRepository.findByIdAndUserTypeAndDeletedAtIsNull(artistId, UserType.ARTIST)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> sessionService.createSession(userService.getUserIdByPublicId(artistPublicId)));
        assertEquals(ExceptionCode.NOT_FOUND.getCode(), exception.getCode());
        assertEquals("해당 아티스트가 존재하지 않습니다.", exception.getMessage());

        verify(userRepository, times(1)).findByIdAndUserTypeAndDeletedAtIsNull(artistId, UserType.ARTIST);
        verify(sessionRepository, never()).findByArtistAndStatus(any(), any());
        verify(sessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("세션 생성 실패 - 이미 진행 중인 세션 존재")
    void createSession_alreadyOpenSession() {
        when(userService.getUserIdByPublicId(artistPublicId)).thenReturn(artistId);
        when(userRepository.findByIdAndUserTypeAndDeletedAtIsNull(artistId, UserType.ARTIST)).thenReturn(Optional.of(artist));
        when(sessionRepository.findByArtistAndStatus(artist, SessionStatus.OPEN)).thenReturn(Optional.of(openSession));

        ApiException exception = assertThrows(ApiException.class, () -> sessionService.createSession(userService.getUserIdByPublicId(artistPublicId)));
        assertEquals(ExceptionCode.CONFLICT.getCode(), exception.getCode());
        assertEquals("이미 진행 중인 신청곡 받기 세션이 있습니다. Session ID: " + sessionId, exception.getMessage());

        verify(userRepository, times(1)).findByIdAndUserTypeAndDeletedAtIsNull(artistId, UserType.ARTIST);
        verify(sessionRepository, times(1)).findByArtistAndStatus(artist, SessionStatus.OPEN);
        verify(sessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("세션 종료 성공")
    void closeSession_success() {
        when(userService.getUserIdByPublicId(artistPublicId)).thenReturn(artistId);
        when(userRepository.findByIdAndUserTypeAndDeletedAtIsNull(artistId, UserType.ARTIST)).thenReturn(Optional.of(artist));
        when(sessionRepository.findByIdAndArtistId(sessionId, artistId)).thenReturn(Optional.of(openSession));

        Long resolvedArtistId = userService.getUserIdByPublicId(artistPublicId);
        SessionResponseDto responseDto = sessionService.closeSession(resolvedArtistId, sessionId);

        assertNotNull(responseDto);
        assertEquals(sessionId, responseDto.getSessionId());
        assertEquals(artistPublicId, responseDto.getArtistId());
        assertEquals(SessionStatus.CLOSE, responseDto.getStatus());
        assertNotNull(responseDto.getStartedAt());
        assertNotNull(openSession.getClosedAt());

        verify(userRepository, times(1)).findByIdAndUserTypeAndDeletedAtIsNull(artistId, UserType.ARTIST);
        verify(sessionRepository, times(1)).findByIdAndArtistId(sessionId, artistId);
    }

    @Test
    @DisplayName("세션 종료 실패 - 아티스트를 찾을 수 없음")
    void closeSession_artistNotFound() {
        // Given
        when(userService.getUserIdByPublicId(artistPublicId)).thenReturn(artistId);
        when(userRepository.findByIdAndUserTypeAndDeletedAtIsNull(artistId, UserType.ARTIST)).thenReturn(Optional.empty());

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () -> sessionService.closeSession(userService.getUserIdByPublicId(artistPublicId), sessionId));
        assertEquals(ExceptionCode.NOT_FOUND.getCode(), exception.getCode());
        assertEquals("아티스트를 찾을 수 없습니다. ID: " + artistId, exception.getMessage());

        verify(userRepository, times(1)).findByIdAndUserTypeAndDeletedAtIsNull(artistId, UserType.ARTIST);
        verify(sessionRepository, never()).findByIdAndArtistId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("세션 종료 실패 - 해당 아티스트의 세션을 찾을 수 없음")
    void closeSession_sessionNotFound() {
        // Given
        when(userService.getUserIdByPublicId(artistPublicId)).thenReturn(artistId);
        when(userRepository.findByIdAndUserTypeAndDeletedAtIsNull(artistId, UserType.ARTIST)).thenReturn(Optional.of(artist));
        when(sessionRepository.findByIdAndArtistId(sessionId, artistId)).thenReturn(Optional.empty());

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () -> sessionService.closeSession(userService.getUserIdByPublicId(artistPublicId), sessionId));
        assertEquals(ExceptionCode.NOT_FOUND.getCode(), exception.getCode());
        assertEquals("해당 아티스트의 세션을 찾을 수 없습니다. Session ID: " + sessionId, exception.getMessage());

        verify(userRepository, times(1)).findByIdAndUserTypeAndDeletedAtIsNull(artistId, UserType.ARTIST);
        verify(sessionRepository, times(1)).findByIdAndArtistId(sessionId, artistId);
    }

    @Test
    @DisplayName("세션 종료 실패 - 이미 종료된 세션")
    void closeSession_alreadyClosed() {
        when(userService.getUserIdByPublicId(artistPublicId)).thenReturn(artistId);
        when(userRepository.findByIdAndUserTypeAndDeletedAtIsNull(artistId, UserType.ARTIST)).thenReturn(Optional.of(artist));
        when(sessionRepository.findByIdAndArtistId(sessionId, artistId)).thenReturn(Optional.of(closedSession));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> sessionService.closeSession(userService.getUserIdByPublicId(artistPublicId), sessionId));
        assertEquals("Session is already closed", exception.getMessage());

        verify(userRepository, times(1)).findByIdAndUserTypeAndDeletedAtIsNull(artistId, UserType.ARTIST);
        verify(sessionRepository, times(1)).findByIdAndArtistId(sessionId, artistId);
    }

    @Test
    @DisplayName("현재 진행 중인 세션 조회 성공")
    void getArtistOpenSession_success() {
        when(userService.getUserIdByPublicId(artistPublicId)).thenReturn(artistId);
        when(userRepository.findByIdAndUserTypeAndDeletedAtIsNull(artistId, UserType.ARTIST)).thenReturn(Optional.of(artist));
        when(sessionRepository.findByArtistIdAndStatus(artistId, SessionStatus.OPEN)).thenReturn(Optional.of(openSession));

        Long resolvedArtistId = userService.getUserIdByPublicId(artistPublicId);
        CurrentSessionResponseDto responseDto = sessionService.getArtistOpenSession(resolvedArtistId);

        assertNotNull(responseDto);
        assertEquals(sessionId, responseDto.getSessionId());
        assertEquals(artistPublicId, responseDto.getArtistId());
        assertEquals(SessionStatus.OPEN, responseDto.getStatus());
        assertNotNull(responseDto.getStartedAt());

        verify(userRepository, times(1)).findByIdAndUserTypeAndDeletedAtIsNull(artistId, UserType.ARTIST);
        verify(sessionRepository, times(1)).findByArtistIdAndStatus(artistId, SessionStatus.OPEN);
    }

    @Test
    @DisplayName("현재 진행 중인 세션 조회 - 진행 중인 세션 없음")
    void getArtistOpenSession_noOpenSession() {
        when(userService.getUserIdByPublicId(artistPublicId)).thenReturn(artistId);
        when(userRepository.findByIdAndUserTypeAndDeletedAtIsNull(artistId, UserType.ARTIST)).thenReturn(Optional.of(artist));
        when(sessionRepository.findByArtistIdAndStatus(artistId, SessionStatus.OPEN)).thenReturn(Optional.empty());

        Long resolvedArtistId = userService.getUserIdByPublicId(artistPublicId);
        CurrentSessionResponseDto responseDto = sessionService.getArtistOpenSession(resolvedArtistId);

        assertNull(responseDto);

        verify(userRepository, times(1)).findByIdAndUserTypeAndDeletedAtIsNull(artistId, UserType.ARTIST);
        verify(sessionRepository, times(1)).findByArtistIdAndStatus(artistId, SessionStatus.OPEN);
    }

    @Test
    @DisplayName("현재 진행 중인 세션 조회 실패 - 아티스트를 찾을 수 없음")
    void getArtistOpenSession_artistNotFound() {
        when(userService.getUserIdByPublicId(artistPublicId)).thenReturn(artistId);
        when(userRepository.findByIdAndUserTypeAndDeletedAtIsNull(artistId, UserType.ARTIST)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> sessionService.getArtistOpenSession(userService.getUserIdByPublicId(artistPublicId)));
        assertEquals(ExceptionCode.NOT_FOUND.getCode(), exception.getCode());
        assertEquals("아티스트를 찾을 수 없습니다. ID: " + artistId, exception.getMessage());

        verify(userRepository, times(1)).findByIdAndUserTypeAndDeletedAtIsNull(artistId, UserType.ARTIST);
        verify(sessionRepository, never()).findByArtistIdAndStatus(anyLong(), any());
    }
}
