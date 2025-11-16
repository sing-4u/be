package com.sing4u.kr.songRequest.service;

import com.sing4u.kr.common.exception.ApiException;
import com.sing4u.kr.common.exception.ExceptionCode;
import com.sing4u.kr.music.MusicInterface;
import com.sing4u.kr.music.MusicPlatformFactory;
import com.sing4u.kr.session.entity.Session;
import com.sing4u.kr.session.enums.SessionStatus;
import com.sing4u.kr.session.repository.SessionRepository;
import com.sing4u.kr.songRequest.dto.request.SongRequestCreateDto;
import com.sing4u.kr.songRequest.dto.SongRequestResponseDto;
import com.sing4u.kr.songRequest.entity.SongRequest;
import com.sing4u.kr.songRequest.repository.SongRequestRepository;
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
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SongRequestServiceTest {

    @InjectMocks
    private SongRequestService songRequestService;

    @Mock
    private SongRequestRepository songRequestRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private MusicInterface spotifyService;

    private SongRequestCreateDto createDto;
    private User artist;
    private Session openSession;
    private final String ARTIST_PUBLIC_ID = "artist_public_id_1";
    private final Long ARTIST_ID = 100L;
    private final Long SESSION_ID = 1L;
    private final String TEST_URL = "https://youtube.com/testurl";

    @BeforeEach
    void setUp() {
        // 공통 테스트 데이터 설정
        createDto = createValidDto();

        // Mock Artist Entity
        artist = User.testUserBuilder(ARTIST_ID, ARTIST_PUBLIC_ID, "Artist", UserType.ARTIST);

        // Mock Open Session Entity
        openSession = Session.builder()
                .id(SESSION_ID)
                .artist(artist)
                .status(SessionStatus.OPEN)
                .songRequests(Collections.emptyList())
                .build();
    }

    // SongRequestCreateDto를 생성하는 헬퍼 메서드
    private SongRequestCreateDto createValidDto() {
        SongRequestCreateDto dto = new SongRequestCreateDto();
        dto.setArtistPublicId(ARTIST_PUBLIC_ID);
        dto.setSessionId(SESSION_ID);
        dto.setSongTitle("Hype Boy");
        dto.setArtistName("NewJeans");
        dto.setEmail("fan@example.com");
        dto.setTags(List.of("#발라드", "#감성"));
        dto.setUrl(TEST_URL); // URL 추가
        return dto;
    }

    // SongRequestCreateDto를 플랫폼 ID 포함으로 생성하는 헬퍼 메서드
    private SongRequestCreateDto createDtoWithPlatformId() {
        SongRequestCreateDto dto = createValidDto();
        dto.setPlatformTrackId("platform_id_123");
        dto.setSongTitle(null); // 외부 정보로 채워지도록 null 설정
        dto.setArtistName(null);
        return dto;
    }

    private SongRequest mockSongRequest(Long id, Session session, String title, String artistName) {
        return SongRequest.builder()
                .id(id)
                .session(session)
                .songTitle(title)
                .songArtistName(artistName)
                .musicPlatformName("SPOTIFY")
                .build();
    }


    // =================================================================================
    // 1. 곡 요청 생성 (`createSongRequest` / `createAndSaveSongRequestInTx`) 테스트
    // =================================================================================

    @Test
    @DisplayName("createSongRequest - URL과 TAGS가 포함된 요청 성공 시, 엔티티에 정확히 저장되는지 검증")
    void createSongRequest_ContainsUrlAndTags_SavesCorrectly() {
        // Given
        SongRequestCreateDto requestDto = createValidDto();
        SongRequest savedRequest = mockSongRequest(1L, openSession, requestDto.getSongTitle(), requestDto.getArtistName());

        given(userService.getUserIdByPublicId(eq(ARTIST_PUBLIC_ID))).willReturn(ARTIST_ID);
        given(sessionRepository.findById(eq(SESSION_ID))).willReturn(Optional.of(openSession));

        // ArgumentCaptor로 save 호출 시의 SongRequest 엔티티를 캡처
        ArgumentCaptor<SongRequest> requestCaptor = ArgumentCaptor.forClass(SongRequest.class);
        given(songRequestRepository.save(requestCaptor.capture())).willReturn(savedRequest);

        // When
        songRequestService.createSongRequest(requestDto);

        // Then
        SongRequest capturedRequest = requestCaptor.getValue();

        // URL 필드 검증
        assertEquals(TEST_URL, capturedRequest.getUrl(), "요청에 포함된 URL이 엔티티에 정확히 저장되어야 합니다.");

        // Tags 필드 검증
        assertNotNull(capturedRequest.getTags(), "태그 리스트가 null이 아니어야 합니다.");
        assertEquals(2, capturedRequest.getTags().size(), "두 개의 태그가 저장되어야 합니다.");
        assertTrue(capturedRequest.getTags().contains("#발라드"), "요청 태그가 포함되어야 합니다.");

        verify(songRequestRepository, times(1)).save(any(SongRequest.class));
    }

    @Test
    @DisplayName("createSongRequest - URL과 TAGS가 null인 경우에도 성공")
    void createSongRequest_NullUrlAndTags_SavesSuccessfully() {
        // Given
        SongRequestCreateDto requestDto = createValidDto();
        requestDto.setUrl(null);
        requestDto.setTags(null);

        SongRequest savedRequest = mockSongRequest(1L, openSession, requestDto.getSongTitle(), requestDto.getArtistName());

        given(userService.getUserIdByPublicId(eq(ARTIST_PUBLIC_ID))).willReturn(ARTIST_ID);
        given(sessionRepository.findById(eq(SESSION_ID))).willReturn(Optional.of(openSession));

        // ArgumentCaptor로 save 호출 시의 SongRequest 엔티티를 캡처
        ArgumentCaptor<SongRequest> requestCaptor = ArgumentCaptor.forClass(SongRequest.class);
        given(songRequestRepository.save(requestCaptor.capture())).willReturn(savedRequest);

        // When
        songRequestService.createSongRequest(requestDto);

        // Then
        SongRequest capturedRequest = requestCaptor.getValue();

        // URL 필드 검증
        assertNull(capturedRequest.getUrl(), "URL이 null로 저장되어야 합니다.");

        // Tags 필드 검증 (null로 저장되는지, 혹은 빈 리스트로 저장되는지는 Entity 설정에 따라 다름)
        // JPA의 @ElementCollection 기본 동작은 null 대신 빈 리스트를 선호할 수 있으나,
        // DTO에서 null을 받았으므로, 현재 엔티티의 Builder 패턴을 따른다면 null이 저장될 가능성이 높습니다.
        assertNull(capturedRequest.getTags(), "태그가 null로 저장되어야 합니다."); // 엔티티 Builder 동작 가정

        verify(songRequestRepository, times(1)).save(any(SongRequest.class));
    }


    // =================================================================================
    // 2. 유효성 검사 (태그 개수) 테스트
    // =================================================================================

    @Test
    @DisplayName("createSongRequest - 태그 개수가 최대 허용치(10개)인 경우 성공")
    void createSongRequest_MaxTags_Success() {
        // Given
        SongRequestCreateDto maxTagsDto = createValidDto();
        List<String> maxTags = List.of("t1", "t2", "t3", "t4", "t5", "t6", "t7", "t8", "t9", "t10");
        maxTagsDto.setTags(maxTags);

        SongRequest savedRequest = mockSongRequest(1L, openSession, maxTagsDto.getSongTitle(), maxTagsDto.getArtistName());

        given(userService.getUserIdByPublicId(eq(ARTIST_PUBLIC_ID))).willReturn(ARTIST_ID);
        given(sessionRepository.findById(eq(SESSION_ID))).willReturn(Optional.of(openSession));
        given(songRequestRepository.save(any(SongRequest.class))).willReturn(savedRequest);

        // When
        SongRequestResponseDto result = songRequestService.createSongRequest(maxTagsDto);

        // Then
        assertNotNull(result);
        verify(songRequestRepository, times(1)).save(argThat(req ->
                req.getTags() != null && req.getTags().size() == 10
        ));
    }


//    @Test
//    @DisplayName("createSongRequest - 태그 개수가 11개로 초과하는 경우 (BAD_REQUEST) 실패")
//    void createSongRequest_TooManyTags_ThrowsApiException() {
//        // Given
//        SongRequestCreateDto tooManyTagsDto = createValidDto();
//        // 11개의 태그
//        tooManyTagsDto.setTags(List.of("t1", "t2", "t3", "t4", "t5", "t6", "t7", "t8", "t9", "t10", "t11"));
//
//        given(userService.getUserIdByPublicId(eq(ARTIST_PUBLIC_ID))).willReturn(ARTIST_ID);
//        given(sessionRepository.findById(eq(SESSION_ID))).willReturn(Optional.of(openSession));
//
//        // When & Then
//        ApiException exception = assertThrows(ApiException.class, () ->
//                songRequestService.createSongRequest(tooManyTagsDto));
//        assertEquals(ExceptionCode.BAD_REQUEST, exception.getCode());
//        assertEquals("태그는 최대 10개까지만 등록 가능합니다.", exception.getMessage());
//
//        // 저장 로직이 호출되지 않았는지 검증
//        verify(songRequestRepository, never()).save(any());
//    }

    // --- 기존의 다른 테스트 코드들은 생략하고 위에 두 섹션에 추가/수정된 테스트만 포함합니다. ---

    // ... (기존의 other tests, e.g., SessionNotFound, ArtistMismatch, ApiSuccess, etc.)

    // =================================================================================
    // 3. 아티스트 곡 요청 목록 조회 (`getSongRequestsByArtist`) 테스트
    // =================================================================================

//    @Test
//    @DisplayName("getSongRequestsByArtist - 아티스트를 찾을 수 없는 경우 (NOT_FOUND)")
//    void getSongRequestsByArtist_ArtistNotFound_ThrowsApiException() {
//        // Given
//        Long artistId = 999L;
//        // Mocking: 아티스트 조회 실패 (findByIdAndUserType)
//        given(userRepository.findByIdAndUserType(eq(artistId), eq(UserType.ARTIST))).willReturn(Optional.empty());
//
//        // When & Then
//        ApiException exception = assertThrows(ApiException.class, () ->
//                songRequestService.getSongRequestsByArtist(artistId));
//        assertEquals(ExceptionCode.NOT_FOUND, exception.getCode());
//        assertEquals("아티스트를 찾을 수 없습니다. ID: 999", exception.getMessage());
//    }

    // ... (기존의 GroupingAndSorting_Success 테스트)
}