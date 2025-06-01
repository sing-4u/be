package com.sing4u.kr.session.controller;

import com.sing4u.kr.common.dto.ResponseResult;
import com.sing4u.kr.common.enums.ResponseCode;
import com.sing4u.kr.session.dto.response.CurrentSessionResponseDto;
import com.sing4u.kr.session.dto.response.SessionResponseDto;
import com.sing4u.kr.session.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/artists")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @PostMapping("/{artistId}/sessions")
    public ResponseResult<SessionResponseDto> startSession(@PathVariable Long artistId) {
        SessionResponseDto sessionResponse = sessionService.createSession(artistId);
        return new ResponseResult<>(ResponseCode.SUCCESS, sessionResponse);
    }

    @PatchMapping("/{artistId}/sessions/{sessionId}/close")
    public ResponseResult<SessionResponseDto> closeSession(@PathVariable Long artistId, @PathVariable Long sessionId) {
        SessionResponseDto sessionResponse = sessionService.closeSession(artistId, sessionId);
        return new ResponseResult<>(ResponseCode.SUCCESS, sessionResponse);
    }

    @GetMapping("/{artistId}/sessions")
    public ResponseResult<CurrentSessionResponseDto> getArtistCurrentOpenSession(@PathVariable Long artistId) {
        CurrentSessionResponseDto currentSession = sessionService.getArtistOpenSession(artistId);
        return new ResponseResult<>(ResponseCode.SUCCESS, currentSession);
    }
}
