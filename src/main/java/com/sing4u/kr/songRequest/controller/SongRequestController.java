package com.sing4u.kr.songRequest.controller;

import com.sing4u.kr.common.dto.ResponseResult;
import com.sing4u.kr.common.enums.ResponseCode;
import com.sing4u.kr.songRequest.dto.request.SongRequestCreateDto;
import com.sing4u.kr.songRequest.dto.SongRequestResponseDto;
import com.sing4u.kr.songRequest.dto.response.ArtistSongRequestsResponse;
import com.sing4u.kr.songRequest.enums.SortType;
import com.sing4u.kr.songRequest.service.SongRequestService;
import com.sing4u.kr.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/songRequests") // API 명세서 기준 Endpoint
@RequiredArgsConstructor
public class SongRequestController {

    private final SongRequestService songRequestService;
    private final UserService userService;

    @Operation(summary = "곡 요청 제출", description = "팬이 아티스트에게 곡 요청을 제출합니다.")
    @PostMapping
    @PreAuthorize("permitAll()")
    public ResponseResult<SongRequestResponseDto> submitSongRequest(@Valid @RequestBody SongRequestCreateDto createDto) {
        SongRequestResponseDto responseDto = songRequestService.createSongRequest(createDto);
        return new ResponseResult<>(ResponseCode.SUCCESS, responseDto);
    }

//    @Operation(summary = "아티스트 곡 요청 목록 조회", description = "특정 아티스트에게 요청된 커스텀 곡 요청 목록을 조회")
//    @GetMapping("/artists/{artistPublicId}")
//    public ResponseResult<List<SessionSongsDto>> getArtistSongRequests(
//            @Parameter(description = "곡 요청 목록을 조회할 아티스트의 공개 ID", example = "user_public_id_1")
//            @PathVariable String artistPublicId) {
//        Long artistId = userService.getUserIdByPublicId(artistPublicId);
//        List<SessionSongsDto> songRequests = songRequestService.getSongRequestsByArtist(artistId);
//        return new ResponseResult<>(ResponseCode.SUCCESS, songRequests);
//    }

    @Operation(
            summary = "아티스트 신청곡 조회(추천 리스트)",
            description = "아티스트에게 들어온 신청곡 목록을 조회합니다. sessionId, keyword, sort(LATEST/POPULAR), page, pageSize"
    )
    @GetMapping("/artists/{artistPublicId}")
    public ResponseResult<ArtistSongRequestsResponse> getArtistSongRequests(
            @PathVariable String artistPublicId,
            @RequestParam(required = false) Long sessionId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "LATEST") @Parameter(description = "정렬 기준", schema = @Schema(implementation = SortType.class)) SortType sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {

        Long artistId = userService.getUserIdByPublicId(artistPublicId);

        ArtistSongRequestsResponse result =
                songRequestService.getArtistSongRequests(artistId, sessionId, keyword, sort, page, pageSize);

        return new ResponseResult<>(ResponseCode.SUCCESS, result);
    }

    @Operation(summary = "신청곡 저장 상태 변경", description = "신청곡 저장 여부를 변경합니다.")
    @PatchMapping("/{songRequestId}/save")
    @PreAuthorize("isAuthenticated()")
    public ResponseResult<Void> changeSaveStatus(
            @PathVariable Long songRequestId,
            @RequestParam boolean saved
    ) {
        songRequestService.changeSaveStatus(songRequestId, saved);
        return new ResponseResult<>(ResponseCode.SUCCESS);
    }

}
