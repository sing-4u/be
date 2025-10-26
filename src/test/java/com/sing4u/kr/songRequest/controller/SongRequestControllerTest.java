package com.sing4u.kr.songRequest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sing4u.kr.common.exception.ApiException;
import com.sing4u.kr.common.exception.ExceptionCode;
import com.sing4u.kr.songRequest.dto.request.SongRequestCreateDto;
import com.sing4u.kr.songRequest.dto.SongRequestResponseDto;
import com.sing4u.kr.songRequest.service.SongRequestService;
import com.sing4u.kr.session.dto.SessionSongsDto;
import com.sing4u.kr.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SongRequestController.class)
class SongRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SongRequestService songRequestService;

    @MockitoBean
    private UserService userService;

    private final String BASE_URL = "/api/v1/songRequests";
    private final String ARTIST_PUBLIC_ID = "artist_public_id_1";
    private final Long ARTIST_ID = 100L;
    private final String TEST_URL = "https://youtube.com/testurl";

    // SongRequestCreateDto를 만드는 헬퍼 메서드
    private SongRequestCreateDto createValidDto() {
        SongRequestCreateDto dto = new SongRequestCreateDto();
        dto.setArtistPublicId(ARTIST_PUBLIC_ID);
        dto.setSessionId(1L);
        dto.setSongTitle("Hype Boy");
        dto.setArtistName("NewJeans");
        dto.setEmail("fan@example.com");
        dto.setTags(List.of("#발라드", "#감성"));
        dto.setUrl(TEST_URL);
        return dto;
    }

    // =================================================================================
    // 1. POST /api/v1/songRequests (곡 요청 제출) 테스트
    // =================================================================================

    @Test
    @DisplayName("POST /submitSongRequest - 유효한 요청 (URL, Tags 포함) 제출 성공")
    @WithMockUser(username = "testuser", roles = {"USER"}) // 인증된 사용자로 Mock킹
    void submitSongRequest_SuccessWithUrlAndTags() throws Exception {
        // Given
        SongRequestCreateDto requestDto = createValidDto();
        SongRequestResponseDto responseDto = new SongRequestResponseDto(1L, "신청곡 추천 등록 완료");

        given(songRequestService.createSongRequest(any(SongRequestCreateDto.class))).willReturn(responseDto);

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .with(csrf()) // CSRF 토큰 추가
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0001"))
                .andExpect(jsonPath("$.data.songRequestId").value(1L));
    }

    @Test
    @DisplayName("POST /submitSongRequest - 유효성 검사 실패 (@NotNull 필드 누락)")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void submitSongRequest_ValidationFail() throws Exception {
        // Given
        SongRequestCreateDto invalidDto = new SongRequestCreateDto();

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .with(csrf()) // CSRF 토큰 추가
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    @Test
    @DisplayName("POST /submitSongRequest - 서비스 유효성 검사 실패 (ApiException 발생)")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void submitSongRequest_ServiceValidationFail() throws Exception {
        // Given
        SongRequestCreateDto requestDto = createValidDto();

        given(songRequestService.createSongRequest(any(SongRequestCreateDto.class)))
                .willThrow(new ApiException(ExceptionCode.CONFLICT, "이 세션은 현재 신청곡을 받고 있지 않습니다."));

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .with(csrf()) // CSRF 토큰 추가
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("이 세션은 현재 신청곡을 받고 있지 않습니다."));
    }

    // =================================================================================
    // 2. GET /api/v1/songRequests/artists/{artistPublicId} (요청 목록 조회) 테스트
    // =================================================================================

    @Test
    @DisplayName("GET /getArtistSongRequests - 아티스트 곡 요청 목록 조회 성공")
    @WithMockUser(username = "testuser", roles = {"ARTIST"}) // 아티스트 권한으로 Mock킹 (필요시)
    void getArtistSongRequests_Success() throws Exception {
        // Given
        List<SessionSongsDto> mockList = Collections.emptyList();

        given(userService.getUserIdByPublicId(eq(ARTIST_PUBLIC_ID))).willReturn(ARTIST_ID);
        given(songRequestService.getSongRequestsByArtist(eq(ARTIST_ID))).willReturn(mockList);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/artists/{artistPublicId}", ARTIST_PUBLIC_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0001"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("GET /getArtistSongRequests - 아티스트 Public ID로 조회 실패 시 (NOT_FOUND)")
    @WithMockUser(username = "testuser", roles = {"ARTIST"})
    void getArtistSongRequests_ArtistNotFound() throws Exception {
        // Given
        given(userService.getUserIdByPublicId(eq(ARTIST_PUBLIC_ID)))
                .willThrow(new ApiException(ExceptionCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // When & Then
        mockMvc.perform(get(BASE_URL + "/artists/{artistPublicId}", ARTIST_PUBLIC_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("사용자를 찾을 수 없습니다."));
    }
}