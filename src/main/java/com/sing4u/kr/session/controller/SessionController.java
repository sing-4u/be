package com.sing4u.kr.session.controller;

import com.sing4u.kr.session.dto.response.CurrentSessionResponseDto;
import com.sing4u.kr.session.dto.response.SessionResponseDto;
import com.sing4u.kr.session.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @PostMapping("/artists/{artistId}/sessions")
    public ResponseEntity<SessionResponseDto> startSession(@PathVariable Long artistId) {
        SessionResponseDto sessionResponse = sessionService.createSession(artistId);
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionResponse);
    }

    @PatchMapping("/artists/{artistId}/sessions/{sessionId}/close")
    public ResponseEntity<SessionResponseDto> closeSession(@PathVariable Long artistId, @PathVariable Long sessionId) {
        SessionResponseDto sessionResponse = sessionService.closeSession(artistId, sessionId);
        return ResponseEntity.ok(sessionResponse);
    }

    @GetMapping("/artists/{artistId}/sessions")
    public ResponseEntity<CurrentSessionResponseDto> getArtistCurrentOpenSession(@PathVariable Long artistId) {
        CurrentSessionResponseDto currentSession = sessionService.getArtistOpenSession(artistId);
        if (currentSession == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(currentSession);
    }
}
