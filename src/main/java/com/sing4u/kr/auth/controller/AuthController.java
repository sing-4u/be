package com.sing4u.kr.auth.controller;

import com.sing4u.kr.auth.dto.LoginDto;
import com.sing4u.kr.auth.dto.request.LoginRequest;
import com.sing4u.kr.auth.dto.response.LoginResponse;
import com.sing4u.kr.auth.dto.response.TokenDto;
import com.sing4u.kr.auth.dto.response.TokenResponse;
import com.sing4u.kr.auth.service.AuthService;
import com.sing4u.kr.auth.utils.CookieUtils;
import com.sing4u.kr.common.dto.ResponseResult;
import com.sing4u.kr.common.enums.ResponseCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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

}
