package com.sing4u.kr.session.controller;

import com.sing4u.kr.common.response.PagingResponse;
import com.sing4u.kr.session.dto.response.ArtistSessionResponseDto;
import com.sing4u.kr.session.enums.SessionStatus;
import com.sing4u.kr.session.service.SessionService;
import com.sing4u.kr.user.repository.UserRepository;
import com.sing4u.kr.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(SessionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class SessionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    SessionService sessionService;

    @MockBean
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @Test
    @DisplayName("아티스트 세션 관리 목록 조회 성공")
    void getArtistSessionsForManage_success() throws Exception {
        // given
        String artistPublicId = "user_public_id_1";
        Long artistId = 1L;

        when(userService.getUserIdByPublicId(artistPublicId))
                .thenReturn(artistId);

        ArtistSessionResponseDto session =
                ArtistSessionResponseDto.builder()
                        .sessionId(1L)
                        .status(SessionStatus.OPEN)
                        .startedAt(LocalDateTime.of(2025, 6, 16, 10, 0))
                        .closedAt(null)
                        .songRequestCount(5L)
                        .build();

        Page<ArtistSessionResponseDto> pageResult =
                new PageImpl<>(
                        List.of(session),
                        PageRequest.of(0, 10),
                        1
                );

        when(sessionService.getArtistSessionsForManage(
                artistId,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startedAt"))
        )).thenReturn(pageResult);

        // when & then
        mockMvc.perform(
                        get("/api/v1/artists/{artistPublicId}/sessions/manage", artistPublicId)
                                .param("page", "0")
                                .param("pageSize", "10")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))

                .andExpect(jsonPath("$.content[0].sessionId").value(1))
                .andExpect(jsonPath("$.content[0].status").value("OPEN"))
                .andExpect(jsonPath("$.content[0].songRequestCount").value(5))

                .andExpect(jsonPath("$.currentPageIndex").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.hasNext").value(false));


    }

}
