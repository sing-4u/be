package com.sing4u.kr.songRequest.service;

import com.sing4u.kr.common.exception.ApiException;
import com.sing4u.kr.common.exception.ExceptionCode;
import com.sing4u.kr.customSongRequest.dto.SongRequestResponseDto;
import com.sing4u.kr.customSongRequest.dto.request.SongRequestCreateDto;
import com.sing4u.kr.customSongRequest.entity.SongRequest;
import com.sing4u.kr.customSongRequest.repository.SongRequestRepository;
import com.sing4u.kr.customSongRequest.service.SongRequestService;
import com.sing4u.kr.session.dto.SessionSongsDto;
import com.sing4u.kr.session.entity.Session;
import com.sing4u.kr.session.enums.SessionStatus;
import com.sing4u.kr.session.repository.SessionRepository;
import com.sing4u.kr.user.entity.User;
import com.sing4u.kr.user.entity.enums.UserType;
import com.sing4u.kr.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SongRequestServiceTest {

    @Mock
    private SongRequestRepository songRequestRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SongRequestService songRequestService;

    private User artist;
    private User otherArtist;
    private Session openSession;
    private Session closedSession;
    private SongRequestCreateDto songRequestCreateDto;

    private final Long ARTIST_ID = 1L;
    private final Long OTHER_ARTIST_ID = 2L;
    private final Long OPEN_SESSION_ID = 100L;
    private final Long CLOSED_SESSION_ID = 101L;
    private final Long SONG_REQUEST_ID = 1L;

    private User createArtist(Long id, String nickName) {
        return User.testUserBuilder(id, nickName, UserType.ARTIST);
    }

    private Session createSession(Long id, User artist, SessionStatus status) {
        return Session.builder()
                .id(id)
                .artist(artist)
                .status(status)
                .startedAt(LocalDateTime.now().minusHours(1))
                .closedAt(status == SessionStatus.CLOSE ? LocalDateTime.now() : null)
                .build();
    }

    private SongRequestCreateDto createSongRequestDto(Long artistId, Long sessionId, String title) {
        SongRequestCreateDto dto = new SongRequestCreateDto();
        dto.setArtistId(artistId);
        dto.setSessionId(sessionId);
        dto.setEmail("fan@example.com");
        dto.setSongTitle(title);
        dto.setArtistName("Test Singer");
        dto.setSpotifyTrackId("spotify:track:123");
        return dto;
    }

    private SongRequest createSongRequest(Long id, Session session, SongRequestCreateDto dto) {
        return SongRequest.builder()
                .id(id)
                .session(session)
                .fanEmail(dto.getEmail())
                .songTitle(dto.getSongTitle())
                .songArtistName(dto.getArtistName())
                .spotifyTrackId(dto.getSpotifyTrackId())
                .requestedAt(LocalDateTime.now())
                .build();
    }

    @BeforeEach
    void setUp() {
        artist = createArtist(ARTIST_ID, "Test Artist");
        otherArtist = createArtist(OTHER_ARTIST_ID, "Other Artist");
        openSession = createSession(OPEN_SESSION_ID, artist, SessionStatus.OPEN);
        closedSession = createSession(CLOSED_SESSION_ID, artist, SessionStatus.CLOSE);
        songRequestCreateDto = createSongRequestDto(ARTIST_ID, OPEN_SESSION_ID, "Test Song");
    }

    @Nested
    @DisplayName("신청곡 생성 (createSongRequest)")
    class CreateSongRequestTests {

        @Test
        @DisplayName("성공")
        void createSongRequest_whenValidRequest_returnsResponseDto() {
            // given
            SongRequest savedSongRequest = createSongRequest(SONG_REQUEST_ID, openSession, songRequestCreateDto);
            when(sessionRepository.findById(OPEN_SESSION_ID)).thenReturn(Optional.of(openSession));
            when(songRequestRepository.save(any(SongRequest.class))).thenReturn(savedSongRequest);

            // when
            SongRequestResponseDto responseDto = songRequestService.createSongRequest(songRequestCreateDto);

            // then
            assertThat(responseDto).isNotNull();
            assertThat(responseDto.getSongRequestId()).isEqualTo(SONG_REQUEST_ID);
            assertThat(responseDto.getMessage()).isEqualTo("신청곡 등록 완료");

            verify(sessionRepository).findById(OPEN_SESSION_ID);
            verify(songRequestRepository).save(any(SongRequest.class));
        }

        @Test
        @DisplayName("실패 - 세션을 찾을 수 없을 때")
        void createSongRequest_whenSessionNotFound_throwsApiException() {
            // given
            when(sessionRepository.findById(OPEN_SESSION_ID)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> songRequestService.createSongRequest(songRequestCreateDto))
                    .isInstanceOf(ApiException.class)
                    .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.NOT_FOUND)
                    .hasMessageContaining("세션을 찾을 수 없습니다.");
            verify(songRequestRepository, never()).save(any(SongRequest.class));
        }

        @Test
        @DisplayName("실패 - 세션의 아티스트와 요청의 아티스트가 다를 때")
        void createSongRequest_whenArtistMismatch_throwsApiException() {
            // given
            SongRequestCreateDto dtoWithMismatchArtist = createSongRequestDto(OTHER_ARTIST_ID, OPEN_SESSION_ID, "Another Song");
            when(sessionRepository.findById(OPEN_SESSION_ID)).thenReturn(Optional.of(openSession)); // openSession의 artist는 ARTIST_ID(1L)

            // when & then
            assertThatThrownBy(() -> songRequestService.createSongRequest(dtoWithMismatchArtist))
                    .isInstanceOf(ApiException.class)
                    .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.CONFLICT)
                    .hasMessageContaining("세션이 지정된 아티스트에게 속하지 않습니다.");
            verify(songRequestRepository, never()).save(any(SongRequest.class));
        }

        @Test
        @DisplayName("실패 - 세션이 OPEN 상태가 아닐 때")
        void createSongRequest_whenSessionNotOpen_throwsApiException() {
            // given
            SongRequestCreateDto dtoForClosedSession = createSongRequestDto(ARTIST_ID, CLOSED_SESSION_ID, "Late Song");
            when(sessionRepository.findById(CLOSED_SESSION_ID)).thenReturn(Optional.of(closedSession)); // closedSession 사용

            // when & then
            assertThatThrownBy(() -> songRequestService.createSongRequest(dtoForClosedSession))
                    .isInstanceOf(ApiException.class)
                    .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.CONFLICT)
                    .hasMessageContaining("이 세션은 현재 신청곡을 받고 있지 않습니다.");
            verify(songRequestRepository, never()).save(any(SongRequest.class));
        }
    }
// SongRequestServiceTest.java - GetSongRequestsByArtistTests 클래스 내

    @Test
    @DisplayName("성공 - 신청곡이 존재할 때")
    void getSongRequestsByArtist_whenSongsExist_returnsListOfSessionSongsDto() {
        // given
        // 1. Artist 모킹
        when(userRepository.findByIdAndUserType(ARTIST_ID, UserType.ARTIST)).thenReturn(Optional.of(artist));

        // 2. sessionRepository.findAllWithSongsByArtist(ARTIST_ID)가 Session 목록을 반환하도록 모킹
        //    (이 Session 객체들은 songRequests 컬렉션을 직접 가지고 있지 않음)
        //    openSession은 @BeforeEach에서 artist와 OPEN 상태로 미리 생성되어 있음
        when(sessionRepository.findAllWithSongsByArtist(ARTIST_ID)).thenReturn(List.of(openSession));

        // 3. 각 세션 ID에 대해 songRequestRepository.findBySessionIdOrderByRequestedAtAsc(sessionId)가
        //    해당 세션의 신청곡 목록을 반환하도록 모킹
        SongRequest song1 = createSongRequest(1L, openSession, createSongRequestDto(ARTIST_ID, OPEN_SESSION_ID, "Song 1"));
        SongRequest song2 = createSongRequest(2L, openSession, createSongRequestDto(ARTIST_ID, OPEN_SESSION_ID, "Song 2"));
        List<SongRequest> requestsForOpenSession = List.of(song1, song2);
        when(songRequestRepository.findBySessionIdOrderByRequestedAtAsc(OPEN_SESSION_ID)).thenReturn(requestsForOpenSession);

        // when
        List<SessionSongsDto> result = songRequestService.getSongRequestsByArtist(ARTIST_ID);

        // then
        assertThat(result).isNotNull().hasSize(1);
        SessionSongsDto sessionSongsDto = result.get(0);
        assertThat(sessionSongsDto.getSessionId()).isEqualTo(OPEN_SESSION_ID);
        assertThat(sessionSongsDto.getSongs()).hasSize(2);
        assertThat(sessionSongsDto.getSongs())
                .extracting(com.sing4u.kr.customSongRequest.dto.response.SongDetailDto::getSongTitle)
                .containsExactlyInAnyOrder("Song 1", "Song 2");

        // verify 호출 확인
        verify(userRepository).findByIdAndUserType(ARTIST_ID, UserType.ARTIST);
        verify(sessionRepository).findAllWithSongsByArtist(ARTIST_ID); // SessionCustomRepository 대신 SessionRepository 사용 가정
        verify(songRequestRepository).findBySessionIdOrderByRequestedAtAsc(OPEN_SESSION_ID); // 이 호출이 발생하는지 확인
    }
    @Nested
    @DisplayName("아티스트별 신청곡 목록 조회 (getSongRequestsByArtist)")
    class GetSongRequestsByArtistTests {

        @Test
        @DisplayName("성공 - 신청곡이 존재할 때")
        void getSongRequestsByArtist_whenSongsExist_returnsListOfSessionSongsDto() {
            when(userRepository.findByIdAndUserType(ARTIST_ID, UserType.ARTIST)).thenReturn(Optional.of(artist));

            when(sessionRepository.findAllWithSongsByArtist(ARTIST_ID)).thenReturn(List.of(openSession));

            SongRequest song1 = createSongRequest(1L, openSession, createSongRequestDto(ARTIST_ID, OPEN_SESSION_ID, "Song 1"));
            SongRequest song2 = createSongRequest(2L, openSession, createSongRequestDto(ARTIST_ID, OPEN_SESSION_ID, "Song 2"));
            List<SongRequest> requestsForOpenSession = List.of(song1, song2);
            when(songRequestRepository.findBySessionIdOrderByRequestedAtAsc(OPEN_SESSION_ID)).thenReturn(requestsForOpenSession);

            // when
            List<SessionSongsDto> result = songRequestService.getSongRequestsByArtist(ARTIST_ID);

            // then
            assertThat(result).isNotNull().hasSize(1);
            SessionSongsDto sessionSongsDto = result.get(0);
            assertThat(sessionSongsDto.getSessionId()).isEqualTo(OPEN_SESSION_ID);
            assertThat(sessionSongsDto.getSongs()).hasSize(2);
            assertThat(sessionSongsDto.getSongs())
                    .extracting(com.sing4u.kr.customSongRequest.dto.response.SongDetailDto::getSongTitle)
                    .containsExactlyInAnyOrder("Song 1", "Song 2");

            // verify 호출 확인
            verify(userRepository).findByIdAndUserType(ARTIST_ID, UserType.ARTIST);
            verify(sessionRepository).findAllWithSongsByArtist(ARTIST_ID); // SessionCustomRepository 대신 SessionRepository 사용 가정
            verify(songRequestRepository).findBySessionIdOrderByRequestedAtAsc(OPEN_SESSION_ID); // 이 호출이 발생하는지 확인
        }

        @Test
        @DisplayName("성공 - 세션은 있으나 신청곡이 없을 때")
        void getSongRequestsByArtist_whenNoSongsInSession_returnsDtoWithEmptySongList() {
            // given
            // openSession.getSongRequests()는 @BeforeEach에서 new ArrayList<>()로 초기화됨
            when(userRepository.findByIdAndUserType(ARTIST_ID, UserType.ARTIST)).thenReturn(Optional.of(artist));
            when(sessionRepository.findAllWithSongsByArtist(ARTIST_ID)).thenReturn(List.of(openSession));


            // when
            List<SessionSongsDto> result = songRequestService.getSongRequestsByArtist(ARTIST_ID);

            // then
            assertThat(result).isNotNull().hasSize(1);
            assertThat(result.get(0).getSongs()).isNotNull().isEmpty();
        }


        @Test
        @DisplayName("성공 - 아티스트에게 세션이 없을 때 빈 목록 반환")
        void getSongRequestsByArtist_whenNoSessionsForArtist_returnsEmptyList() {
            // given
            when(userRepository.findByIdAndUserType(ARTIST_ID, UserType.ARTIST)).thenReturn(Optional.of(artist));
            when(sessionRepository.findAllWithSongsByArtist(ARTIST_ID)).thenReturn(Collections.emptyList());

            // when
            List<SessionSongsDto> result = songRequestService.getSongRequestsByArtist(ARTIST_ID);

            // then
            assertThat(result).isNotNull().isEmpty();
        }


        @Test
        @DisplayName("실패 - 아티스트를 찾을 수 없을 때")
        void getSongRequestsByArtist_whenArtistNotFound_throwsApiException() {
            // given
            when(userRepository.findByIdAndUserType(ARTIST_ID, UserType.ARTIST)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> songRequestService.getSongRequestsByArtist(ARTIST_ID))
                    .isInstanceOf(ApiException.class)
                    .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.NOT_FOUND)
                    .hasMessageContaining("아티스트를 찾을 수 없습니다.");
            verify(sessionRepository, never()).findAllWithSongsByArtist(anyLong());
        }
    }
}