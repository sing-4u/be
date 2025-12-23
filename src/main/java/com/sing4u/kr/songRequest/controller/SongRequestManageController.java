package com.sing4u.kr.songRequest.controller;

import com.sing4u.kr.songRequest.dto.response.SongRequestManageResponseDto;
import com.sing4u.kr.songRequest.service.SongRequestService;
import com.sing4u.kr.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SongRequestManageController {

    private final SongRequestService songRequestService;
    private final UserService userService;

    @Operation(
            summary = "아티스트 신청곡 조회(추천 데이터)",
            description = "추천 데이터 탭에서 신청곡을 집계(총 좋아요 수, 총 추천 수)하여 조회합니다. (keyword는 optional)"
    )
    @GetMapping("/artists/{artistPublicId}/song-requests/manage")
    public SongRequestManageResponseDto getArtistSongRequestsManage(
            @Parameter(description = "아티스트 공개 ID", example = "user_public_id_1")
            @PathVariable String artistPublicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword
    ) {
        Long artistId = userService.getUserIdByPublicId(artistPublicId);
        return songRequestService.getArtistSongRequestsForManage(artistId, page, pageSize, keyword);
    }
}
