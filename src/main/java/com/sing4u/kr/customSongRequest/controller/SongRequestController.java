package com.sing4u.kr.customSongRequest.controller;

import com.sing4u.kr.customSongRequest.dto.request.SongRequestCreateDto;
import com.sing4u.kr.customSongRequest.dto.SongRequestResponseDto;
import com.sing4u.kr.customSongRequest.service.SongRequestService;
import com.sing4u.kr.session.dto.SessionSongsDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/songRequests") // API 명세서 기준 Endpoint
@RequiredArgsConstructor
public class SongRequestController {

    private final SongRequestService songRequestService;

    @PostMapping
    public ResponseEntity<SongRequestResponseDto> submitSongRequest(@Valid @RequestBody SongRequestCreateDto createDto) {
        SongRequestResponseDto responseDto = songRequestService.createSongRequest(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/artists/{artistId}")
    public ResponseEntity<List<SessionSongsDto>> getArtistSongRequests(@PathVariable Long artistId) {
        List<SessionSongsDto> songRequests = songRequestService.getSongRequestsByArtist(artistId);
        if (songRequests == null || songRequests.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(songRequests);
    }
}
