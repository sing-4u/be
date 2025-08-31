package com.sing4u.kr.auth.controller;

import com.sing4u.kr.auth.dto.LoginDto;
import com.sing4u.kr.auth.dto.request.LoginRequest;
import com.sing4u.kr.auth.dto.request.PasswordResetConfirmRequest;
import com.sing4u.kr.auth.dto.request.PasswordResetSendCodeRequest;
import com.sing4u.kr.auth.dto.request.PasswordResetVerifyRequest;
import com.sing4u.kr.auth.dto.response.LoginResponse;
import com.sing4u.kr.auth.dto.response.SendCodeResponse;
import com.sing4u.kr.auth.dto.response.TokenDto;
import com.sing4u.kr.auth.dto.response.TokenResponse;
import com.sing4u.kr.auth.service.AuthService;
import com.sing4u.kr.auth.utils.CookieUtils;
import com.sing4u.kr.common.auth.LoginUserId;
import com.sing4u.kr.common.dto.ResponseResult;
import com.sing4u.kr.common.enums.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthService service;

    @PostMapping("/login/email")
    public ResponseResult<LoginResponse> emailLogin(@RequestBody LoginRequest request,
                                                    HttpServletResponse response) {
        LoginDto dto = authService.emailLogin(request);
        CookieUtils.setRefreshTokenCookie(response, dto.getRefreshToken());
        CookieUtils.setAccessTokenCookie(response, dto.getAccessToken());

        return new ResponseResult<>(ResponseCode.SUCCESS, LoginResponse.of(dto.getAccessToken(), dto.getProfileImage()));
    }

    @PostMapping("/refresh")
    public ResponseResult<TokenResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtils.extractRefreshTokenFromCookie(request);
        TokenDto dto = authService.refresh(refreshToken);
        CookieUtils.setRefreshTokenCookie(response, dto.getRefreshToken());
        return new ResponseResult<>(ResponseCode.SUCCESS, TokenResponse.of(dto.getAccessToken(), dto.getRefreshToken()));
    }

    @Operation(summary = "로그아웃", description = "사용자의 AccessToken / RefreshToken 쿠키를 삭제합니다.")
    @PostMapping("/logout")
    public ResponseResult<Void> logout(@LoginUserId Long userId, HttpServletResponse response) {
        // RefreshToken db에서 삭제
        authService.logout(userId);

        // 쿠키 삭제: accessToken / refreshToken
        CookieUtils.deleteAccessTokenCookie(response);
        CookieUtils.deleteRefreshTokenCookie(response);

        return new ResponseResult<>(ResponseCode.SUCCESS);
    }

    @Operation(
            summary = "인증번호 전송 (3분 유효, 30초 재전송 제한)",
            description = """
            입력한 이메일로 6자리 인증번호를 전송합니다.
            동일 이메일 재전송은 30초 제한이 있습니다.
            
            에러 코드
            - 1201: 재전송 제한(잠시 후 다시 시도)
            - 1205: SNS 간편가입 계정
            """
    )
    @PostMapping("/password-reset/code")
    public ResponseResult<SendCodeResponse> sendCode(@RequestBody @Valid PasswordResetSendCodeRequest req) {
        SendCodeResponse data = service.sendPasswordResetCode(req.getEmail());
        return new ResponseResult<>(ResponseCode.SUCCESS, data);
    }


    @Operation(
            summary = "인증번호 검증 (성공 시 resetToken 발급)",
            description = """
            이메일과 6자리 인증번호를 검증합니다.
            성공 시 10분 유효의 resetToken을 반환합니다.
            
            에러 코드
            - 1202: 코드 만료
            - 1203: 코드 불일치
            """
    )
    @PostMapping("/password-reset/verify")
    public ResponseResult<Map<String, String>> verify(@RequestBody @Valid PasswordResetVerifyRequest req) {
        String ticket = service.verifyPasswordResetCode(req.getEmail(), req.getCode());
        return new ResponseResult<>(ResponseCode.SUCCESS, Map.of("resetToken", ticket));
    }

    @Operation(
            summary = "비밀번호 변경(이메일 인증 후)",
            description = """
            email + resetToken + newPassword 를 전달하여 비밀번호를 최종 변경합니다.
            
            에러 코드
            - 1204: resetToken 불일치 또는 만료
            """
    )
    @PostMapping("/password-reset/confirm")
    public ResponseResult<Void> confirm(@RequestBody @Valid PasswordResetConfirmRequest req) {
        service.confirmPasswordReset(req.getEmail(), req.getResetToken(), req.getNewPassword());
        return new ResponseResult<>(ResponseCode.SUCCESS);
    }
}
