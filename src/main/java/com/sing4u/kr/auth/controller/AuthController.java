package com.sing4u.kr.auth.controller;

import com.sing4u.kr.auth.dto.LoginDto;
import com.sing4u.kr.auth.dto.request.LoginRequest;
import com.sing4u.kr.auth.dto.response.LoginResponse;
import com.sing4u.kr.auth.dto.response.TokenDto;
import com.sing4u.kr.auth.dto.response.TokenResponse;
import com.sing4u.kr.auth.service.AuthService;
import com.sing4u.kr.common.dto.ResponseResult;
import com.sing4u.kr.common.enums.ResponseCode;
import com.sing4u.kr.jwt.exceptions.InvalidTokenException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    @Value("${cookie.refresh-name}")
    private String refreshCookieName;

    @Value("${cookie.refresh-path}")
    private String refreshPath;

    @Value("${cookie.refresh-http-only}")
    private boolean refreshHttpOnly;

    @Value("${cookie.refresh-secure}")
    private boolean refreshSecure;

    @Value("${cookie.refresh-same-site}")
    private String refreshSameSite;

    @Value("${cookie.refresh-max-age-days}")
    private int refreshMaxAgeDays;

    private final AuthService authService;

    @PostMapping("/login/email")
    public ResponseResult<LoginResponse> emailLogin(@RequestBody LoginRequest request,
                                                    HttpServletResponse response) {
        LoginDto dto = authService.emailLogin(request);
        setRefreshTokenCookie(response, dto.getRefreshToken());

        return new ResponseResult<>(ResponseCode.SUCCESS, LoginResponse.of(dto.getAccessToken(), dto.getProfileImage()));
    }

    @PostMapping("/recreate")
    public ResponseResult<TokenResponse> recreate(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        TokenDto dto = authService.recreate(refreshToken);
        setRefreshTokenCookie(response, dto.getRefreshToken());
        return new ResponseResult<>(ResponseCode.SUCCESS, TokenResponse.of(dto.getAccessToken(), dto.getRefreshToken()));
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (refreshCookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        throw new InvalidTokenException();
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(refreshCookieName, refreshToken)
                .httpOnly(refreshHttpOnly)
                .secure(refreshSecure)
                .sameSite(refreshSameSite)
                .path(refreshPath)
                .maxAge(Duration.ofDays(refreshMaxAgeDays))
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

}
