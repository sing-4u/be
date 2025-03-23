package com.sing4u.kr.songrequest.ui;

import com.sing4u.kr.songrequest.application.command.RequestSongUseCase;
import com.sing4u.kr.songrequest.application.dto.RequestSongCommand;
import com.sing4u.kr.songrequest.application.dto.SongRankResponse;
import com.sing4u.kr.songrequest.application.dto.SongRequestResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/song-requests")
public class SongRequestController {

    private final RequestSongUseCase requestSongUseCase;

    public SongRequestController(RequestSongUseCase requestSongUseCase) {
        this.requestSongUseCase = requestSongUseCase;
    }

    @PostMapping
    public ResponseEntity<SongRequestResponse> requestSong(@RequestBody RequestSongCommand command) {
        return ResponseEntity.ok(requestSongUseCase.execute(command));
    }
}