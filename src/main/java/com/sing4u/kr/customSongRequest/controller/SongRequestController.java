package com.sing4u.kr.customSongRequest.controller;

import com.sing4u.kr.common.dto.ResponseResult;
import com.sing4u.kr.common.enums.ResponseCode;
import com.sing4u.kr.customSongRequest.dto.request.SongRequestCreateDto;
import com.sing4u.kr.customSongRequest.dto.SongRequestResponseDto;
import com.sing4u.kr.customSongRequest.service.SongRequestService;
import com.sing4u.kr.session.dto.SessionSongsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/songRequests") // API 명세서 기준 Endpoint
@RequiredArgsConstructor
public class SongRequestController {

    private final SongRequestService songRequestService;

    @Operation(summary = "곡 요청 제출", description = "팬이 아티스트에게 곡 요청을 제출합니다.")
    @PostMapping
    public ResponseResult<SongRequestResponseDto> submitSongRequest(@Valid @RequestBody SongRequestCreateDto createDto) {
        SongRequestResponseDto responseDto = songRequestService.createSongRequest(createDto);
        return new ResponseResult<>(ResponseCode.SUCCESS, responseDto);
    }

    @Operation(summary = "아티스트 곡 요청 목록 조회", description = "특정 아티스트에게 요청된 커스텀 곡 요청 목록을 조회")
    @GetMapping("/artists/{artistId}")
    public ResponseResult<List<SessionSongsDto>> getArtistSongRequests(
            @Parameter(description = "곡 요청 목록을 조회할 아티스트의 ID", example = "1")
            @PathVariable Long artistId) {
        List<SessionSongsDto> songRequests = songRequestService.getSongRequestsByArtist(artistId);
        return new ResponseResult<>(ResponseCode.SUCCESS, songRequests);
    }
}
