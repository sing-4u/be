package com.sing4u.kr.songRequest.controller;

import com.sing4u.kr.songRequest.dto.response.SongRequestManageItemDto;
import com.sing4u.kr.songRequest.dto.response.SongRequestManageResponseDto;
import com.sing4u.kr.songRequest.service.SongRequestService;
import com.sing4u.kr.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(SongRequestManageController.class)
@AutoConfigureMockMvc(addFilters = false)
public class SongRequestManageControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    SongRequestService songRequestService;

    @MockBean
    UserService userService;

    @Test
    @DisplayName("/get 아티스트 신청곡 조회(추천 데이터) 성공")
    void getArtistSongRequestsManage_success() throws Exception {
        String artistPublicId = "user_public_id_1";
        Long artistId = 10L;

        when(userService.getUserIdByPublicId(artistPublicId))
                .thenReturn(artistId);

        SongRequestManageItemDto item = new SongRequestManageItemDto(
                1001L,
                "Blue Valentine",
                "NMIXX",
                13L,
                "https://i.scdn.co/image/ab67616d00",
                4L
        );

        SongRequestManageResponseDto response =
                SongRequestManageResponseDto.builder()
                        .songs(List.of(item))
                        .hasNext(false)
                        .page(0)
                        .pageSize(10)
                        .totalElements(1L)
                        .build();

        when(songRequestService.getArtistSongRequestsForManage(
                eq(artistId),
                eq(0),
                eq(10),
                isNull()
        )).thenReturn(response);

        mockMvc.perform(
                        get("/api/v1/artists/{artistPublicId}/song-requests/manage", artistPublicId)
                                .param("page", "0")
                                .param("pageSize", "10")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(jsonPath("$.data.songs").isArray())
                .andExpect(jsonPath("$.data.songs[0].songRequestId").value(1001))
                .andExpect(jsonPath("$.data.songs[0].songTitle").value("Blue Valentine"))
                .andExpect(jsonPath("$.data.songs[0].singer").value("NMIXX"))
                .andExpect(jsonPath("$.data.songs[0].totalLikeCount").value(13))
                .andExpect(jsonPath("$.data.songs[0].totalRequestCount").value(4))
                .andExpect(jsonPath("$.data.hasNext").value(false))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.pageSize").value(10))
                .andExpect(jsonPath("$.data.totalElements").value(1));

    }

    @Test
    @DisplayName("/get 아티스트 신청곡 조회(추천 데이터) - keyword 포함")
    void getArtistSongRequestsManage_withKeyword() throws Exception {
        String artistPublicId = "user_public_id_1";
        Long artistId = 10L;

        when(userService.getUserIdByPublicId(artistPublicId))
                .thenReturn(artistId);

        SongRequestManageResponseDto response =
                SongRequestManageResponseDto.builder()
                        .songs(List.of())
                        .hasNext(false)
                        .page(0)
                        .pageSize(10)
                        .totalElements(0L)
                        .build();

        when(songRequestService.getArtistSongRequestsForManage(
                eq(artistId),
                eq(0),
                eq(10),
                eq("blue")
        )).thenReturn(response);

        mockMvc.perform(
                        get("/api/v1/artists/{artistPublicId}/song-requests/manage", artistPublicId)
                                .param("page", "0")
                                .param("pageSize", "10")
                                .param("keyword", "blue")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.songs").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(0));

    }

}
