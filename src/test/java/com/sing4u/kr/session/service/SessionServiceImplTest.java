//package com.sing4u.kr.session.service;
//
//import com.sing4u.kr.common.exception.ApiException;
//import com.sing4u.kr.common.exception.ExceptionCode;
//import com.sing4u.kr.session.dto.response.CurrentSessionResponseDto;
//import com.sing4u.kr.session.dto.response.SessionResponseDto;
//import com.sing4u.kr.session.entity.Session;
//import com.sing4u.kr.session.enums.SessionStatus;
//import com.sing4u.kr.session.repository.SessionRepository;
//import com.sing4u.kr.user.entity.User;
//import com.sing4u.kr.user.entity.enums.UserType;
//import com.sing4u.kr.user.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class SessionServiceImplTest {
//
//    @Mock
//    private SessionRepository sessionRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private SessionService sessionService;
//
//    private User artist;
//    private final Long ARTIST_ID = 1L;
//    private final Long NON_EXISTENT_ARTIST_ID = 99L;
//    private final Long SESSION_ID = 100L;
//    private final Long NON_EXISTENT_SESSION_ID = 999L;
//
//    private User createArtist(Long id, String nickName) {
//        return User.testUserBuilder(id, nickName, UserType.ARTIST);
//    }
//
//    private Session createSession(Long id, User artist, SessionStatus status) {
//        LocalDateTime startedAt = LocalDateTime.now().minusHours(1);
//        LocalDateTime closedAt = (status == SessionStatus.CLOSE) ? startedAt.plusMinutes(30) : null;
//        return Session.builder()
//                .id(id)
//                .artist(artist)
//                .status(status)
//                .startedAt(startedAt)
//                .closedAt(closedAt)
//                .build();
//    }
//
//    @BeforeEach
//    void setUp() {
//        artist = createArtist(ARTIST_ID, "Test Artist");
//    }
//
//    @Nested
//    @DisplayName("세션 생성 (createSession)")
//    class CreateSessionTests {
//
//        @Test
//        @DisplayName("성공 - 아티스트가 존재하고 열린 세션이 없을 때")
//        void createSession_whenArtistExistsAndNoOpenSession_returnsSessionResponseDto() {
//            // given
//            Session newSession = Session.create(artist); // Service 로직에서 호출되는 부분
//            Session savedSession = createSession(SESSION_ID, artist, SessionStatus.OPEN); // save 후 반환될 객체 모킹
//            // ID와 startedAt이 설정된 상태
//            savedSession.setStartedAt(newSession.getStartedAt()); // Session.create()에서 설정된 startedAt 사용
//
//            when(userRepository.findByIdAndUserType(ARTIST_ID, UserType.ARTIST)).thenReturn(Optional.of(artist));
//            when(sessionRepository.findByArtistAndStatus(artist, SessionStatus.OPEN)).thenReturn(Optional.empty());
//            when(sessionRepository.save(any(Session.class))).thenReturn(savedSession);
//
//            // when
//            SessionResponseDto responseDto = sessionService.createSession(ARTIST_ID);
//
//            // then
//            assertThat(responseDto).isNotNull();
//            assertThat(responseDto.getArtistId()).isEqualTo(ARTIST_ID);
//            assertThat(responseDto.getStatus()).isEqualTo(SessionStatus.OPEN);
//            assertThat(responseDto.getSessionId()).isEqualTo(SESSION_ID);
//            assertThat(responseDto.getStartedAt()).isEqualTo(savedSession.getStartedAt());
//
//            verify(userRepository).findByIdAndUserType(ARTIST_ID, UserType.ARTIST);
//            verify(sessionRepository).findByArtistAndStatus(artist, SessionStatus.OPEN);
//            verify(sessionRepository).save(any(Session.class));
//        }
//
//        @Test
//        @DisplayName("실패 - 존재하지 않는 아티스트 ID일 때 ApiException 발생")
//        void createSession_whenArtistNotFound_throwsApiException() {
//            // given
//            when(userRepository.findByIdAndUserType(NON_EXISTENT_ARTIST_ID, UserType.ARTIST)).thenReturn(Optional.empty());
//
//            // when & then
//            assertThatThrownBy(() -> sessionService.createSession(NON_EXISTENT_ARTIST_ID))
//                    .isInstanceOf(ApiException.class)
//                    .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.NOT_FOUND)
//                    .hasMessageContaining("해당 아티스트가 존재하지 않습니다.");
//            verify(sessionRepository, never()).findByArtistAndStatus(any(), any());
//            verify(sessionRepository, never()).save(any(Session.class));
//        }
//
//        @Test
//        @DisplayName("실패 - 이미 열린 세션이 존재할 때 ApiException 발생")
//        void createSession_whenOpenSessionExists_throwsApiException() {
//            // given
//            Session existingOpenSession = createSession(SESSION_ID, artist, SessionStatus.OPEN);
//            when(userRepository.findByIdAndUserType(ARTIST_ID, UserType.ARTIST)).thenReturn(Optional.of(artist));
//            when(sessionRepository.findByArtistAndStatus(artist, SessionStatus.OPEN)).thenReturn(Optional.of(existingOpenSession));
//
//            // when & then
//            assertThatThrownBy(() -> sessionService.createSession(ARTIST_ID))
//                    .isInstanceOf(ApiException.class)
//                    .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.CONFLICT)
//                    .hasMessageContaining("이미 진행 중인 신청곡 받기 세션이 있습니다.");
//            verify(sessionRepository, never()).save(any(Session.class));
//        }
//    }
//
//    @Nested
//    @DisplayName("세션 종료 (closeSession)")
//    class CloseSessionTests {
//        private Session openSession;
//
//        @BeforeEach
//        void nestedSetUp() {
//            openSession = createSession(SESSION_ID, artist, SessionStatus.OPEN);
//        }
//
//        @Test
//        @DisplayName("성공 - 열린 세션이 존재할 때")
//        void closeSession_whenOpenSessionExists_returnsClosedSessionResponseDto() {
//            // given
//            when(userRepository.findByIdAndUserType(ARTIST_ID, UserType.ARTIST)).thenReturn(Optional.of(artist));
//            when(sessionRepository.findByIdAndArtistId(SESSION_ID, ARTIST_ID)).thenReturn(Optional.of(openSession));
//
//            // when
//            SessionResponseDto responseDto = sessionService.closeSession(ARTIST_ID, SESSION_ID);
//
//            // then
//            assertThat(responseDto).isNotNull();
//            assertThat(responseDto.getSessionId()).isEqualTo(SESSION_ID);
//            assertThat(responseDto.getStatus()).isEqualTo(SessionStatus.CLOSE);
//            assertThat(responseDto.getClosedAt()).isNotNull();
//
//            assertThat(openSession.getStatus()).isEqualTo(SessionStatus.CLOSE); // 상태 변경 확인
//            assertThat(openSession.getClosedAt()).isNotNull(); // 종료 시간 설정 확인
//
//            verify(userRepository).findByIdAndUserType(ARTIST_ID, UserType.ARTIST);
//            verify(sessionRepository).findByIdAndArtistId(SESSION_ID, ARTIST_ID);
//        }
//
//        @Test
//        @DisplayName("실패 - 아티스트를 찾을 수 없을 때 ApiException 발생")
//        void closeSession_whenArtistNotFound_throwsApiException() {
//            // given
//            when(userRepository.findByIdAndUserType(NON_EXISTENT_ARTIST_ID, UserType.ARTIST)).thenReturn(Optional.empty());
//
//            // when & then
//            assertThatThrownBy(() -> sessionService.closeSession(NON_EXISTENT_ARTIST_ID, SESSION_ID))
//                    .isInstanceOf(ApiException.class)
//                    .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.NOT_FOUND);
//            verify(sessionRepository, never()).findByIdAndArtistId(anyLong(), anyLong());
//        }
//
//        @Test
//        @DisplayName("실패 - 세션을 찾을 수 없을 때 ApiException 발생")
//        void closeSession_whenSessionNotFound_throwsApiException() {
//            // given
//            when(userRepository.findByIdAndUserType(ARTIST_ID, UserType.ARTIST)).thenReturn(Optional.of(artist));
//            when(sessionRepository.findByIdAndArtistId(NON_EXISTENT_SESSION_ID, ARTIST_ID)).thenReturn(Optional.empty());
//
//            // when & then
//            assertThatThrownBy(() -> sessionService.closeSession(ARTIST_ID, NON_EXISTENT_SESSION_ID))
//                    .isInstanceOf(ApiException.class)
//                    .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.NOT_FOUND);
//        }
//
//        @Test
//        @DisplayName("실패 - 세션이 이미 닫혀있을 때 IllegalStateException 발생")
//        void closeSession_whenSessionAlreadyClosed_throwsIllegalStateException() {
//            // given
//            Session alreadyClosedSession = createSession(SESSION_ID, artist, SessionStatus.CLOSE);
//            when(userRepository.findByIdAndUserType(ARTIST_ID, UserType.ARTIST)).thenReturn(Optional.of(artist));
//            when(sessionRepository.findByIdAndArtistId(SESSION_ID, ARTIST_ID)).thenReturn(Optional.of(alreadyClosedSession));
//
//            // when & then
//            assertThatThrownBy(() -> sessionService.closeSession(ARTIST_ID, SESSION_ID))
//                    .isInstanceOf(IllegalStateException.class)
//                    .hasMessageContaining("Session is already closed");
//        }
//    }
//
//    @Nested
//    @DisplayName("아티스트 현재 열린 세션 조회 (getArtistCurrentOpenSession)")
//    class GetArtistCurrentOpenSessionTests {
//
//        @Test
//        @DisplayName("성공 - 열린 세션이 존재할 때")
//        void getArtistCurrentOpenSession_whenOpenSessionExists_returnsDto() {
//            // given
//            Session openSession = createSession(SESSION_ID, artist, SessionStatus.OPEN);
//            when(userRepository.findByIdAndUserType(ARTIST_ID, UserType.ARTIST)).thenReturn(Optional.of(artist));
//            when(sessionRepository.findByArtistIdAndStatus(ARTIST_ID, SessionStatus.OPEN)).thenReturn(Optional.of(openSession));
//
//            // when
//            CurrentSessionResponseDto responseDto = sessionService.getArtistOpenSession(ARTIST_ID);
//
//            // then
//            assertThat(responseDto).isNotNull();
//            assertThat(responseDto.getSessionId()).isEqualTo(SESSION_ID);
//            assertThat(responseDto.getArtistId()).isEqualTo(ARTIST_ID);
//            assertThat(responseDto.getStatus()).isEqualTo(SessionStatus.OPEN);
//            assertThat(responseDto.getStartedAt()).isEqualTo(openSession.getStartedAt());
//
//            verify(userRepository).findByIdAndUserType(ARTIST_ID, UserType.ARTIST);
//            verify(sessionRepository).findByArtistIdAndStatus(ARTIST_ID, SessionStatus.OPEN);
//        }
//
//        @Test
//        @DisplayName("성공 - 열린 세션이 없을 때 null 반환")
//        void getArtistCurrentOpenSession_whenNoOpenSession_returnsNull() {
//            // given
//            when(userRepository.findByIdAndUserType(ARTIST_ID, UserType.ARTIST)).thenReturn(Optional.of(artist));
//            when(sessionRepository.findByArtistIdAndStatus(ARTIST_ID, SessionStatus.OPEN)).thenReturn(Optional.empty());
//
//            // when
//            CurrentSessionResponseDto responseDto = sessionService.getArtistOpenSession(ARTIST_ID);
//
//            // then
//            assertThat(responseDto).isNull();
//        }
//
//        @Test
//        @DisplayName("실패 - 아티스트를 찾을 수 없을 때 ApiException 발생")
//        void getArtistCurrentOpenSession_whenArtistNotFound_throwsApiException() {
//            // given
//            when(userRepository.findByIdAndUserType(NON_EXISTENT_ARTIST_ID, UserType.ARTIST)).thenReturn(Optional.empty());
//
//            // when & then
//            assertThatThrownBy(() -> sessionService.getArtistOpenSession(NON_EXISTENT_ARTIST_ID))
//                    .isInstanceOf(ApiException.class)
//                    .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.NOT_FOUND);
//            verify(sessionRepository, never()).findByArtistIdAndStatus(anyLong(), any(SessionStatus.class));
//        }
//    }
//}