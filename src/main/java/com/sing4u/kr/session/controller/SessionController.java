package com.sing4u.kr.session.controller;

import com.sing4u.kr.common.dto.ResponseResult;
import com.sing4u.kr.common.enums.ResponseCode;
import com.sing4u.kr.session.dto.response.CurrentSessionResponseDto;
import com.sing4u.kr.session.dto.response.SessionResponseDto;
import com.sing4u.kr.session.service.SessionService;
import com.sing4u.kr.user.entity.User;
import com.sing4u.kr.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/artists")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final UserService userService;

    @Operation(summary = "세션 시작", description = "아티스트가 신청곡 받기를 시작합니다.")
    @PostMapping("/{artistPublicId}/sessions")
    public ResponseResult<SessionResponseDto> startSession(
            @Parameter(description = "세션을 시작할 아티스트의 공개 ID", example = "user_public_id_3")
            @PathVariable String artistPublicId) {
        Long artistId = userService.getUserIdByPublicId(artistPublicId);
        SessionResponseDto sessionResponse = sessionService.createSession(artistId);
        return new ResponseResult<>(ResponseCode.SUCCESS, sessionResponse);
    }

    @Operation(summary = "세션 종료", description = "신청곡 받기를 종료 처리합니다.")
    @PatchMapping("/{artistPublicId}/sessions/{sessionId}/close")
    public ResponseResult<SessionResponseDto> closeSession(
            @Parameter(description = "세션을 종료할 아티스트의 공개 ID", example = "user_public_id_1")
            @PathVariable String artistPublicId,
            @Parameter(description = "종료할 세션의 ID", example = "1")
            @PathVariable Long sessionId) {
        Long artistId = userService.getUserIdByPublicId(artistPublicId);
        SessionResponseDto sessionResponse = sessionService.closeSession(artistId, sessionId);
        return new ResponseResult<>(ResponseCode.SUCCESS, sessionResponse);
    }

    @Operation(summary = "현재 오픈된 세션 조회", description = "아티스트의 현재 진행 중인 세션 정보를 조회합니다.")
    @GetMapping("/{artistPublicId}/sessions")
    public ResponseResult<CurrentSessionResponseDto> getArtistCurrentOpenSession(
            @Parameter(description = "세션 정보를 조회할 아티스트의 공개 ID", example = "user_public_id_1")
            @PathVariable String artistPublicId) {
        Long artistId = userService.getUserIdByPublicId(artistPublicId);
        CurrentSessionResponseDto currentSession = sessionService.getArtistOpenSession(artistId);

        if (currentSession == null) {
            return new ResponseResult<>(ResponseCode.SUCCESS, "오픈된 세션이 없습니다.", null);
        }

        return new ResponseResult<>(ResponseCode.SUCCESS, currentSession);
    }
}
