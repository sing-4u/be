package com.sing4u.kr.customSongRequest.controller;

import com.sing4u.kr.common.dto.ResponseResult;
import com.sing4u.kr.common.enums.ResponseCode;
import com.sing4u.kr.customSongRequest.dto.request.SongRequestCreateDto;
import com.sing4u.kr.customSongRequest.dto.SongRequestResponseDto;
import com.sing4u.kr.customSongRequest.service.SongRequestService;
import com.sing4u.kr.session.dto.SessionSongsDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/songRequests") // API 명세서 기준 Endpoint
@RequiredArgsConstructor
public class SongRequestController {

    private final SongRequestService songRequestService;

    @PostMapping
    public ResponseResult<SongRequestResponseDto> submitSongRequest(@Valid @RequestBody SongRequestCreateDto createDto) {
        SongRequestResponseDto responseDto = songRequestService.createSongRequest(createDto);
        return new ResponseResult<>(ResponseCode.SUCCESS, responseDto);
    }

    @GetMapping("/artists/{artistId}")
    public ResponseResult<List<SessionSongsDto>> getArtistSongRequests(@PathVariable Long artistId) {
        List<SessionSongsDto> songRequests = songRequestService.getSongRequestsByArtist(artistId);
        return new ResponseResult<>(ResponseCode.SUCCESS, songRequests);
    }
}
