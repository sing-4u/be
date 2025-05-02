package com.sing4u.kr.auth.controller;

import com.sing4u.kr.auth.dto.LoginTokenDto;
import com.sing4u.kr.auth.dto.request.LoginRequest;
import com.sing4u.kr.auth.dto.response.LoginResponse;
import com.sing4u.kr.auth.service.AuthService;
import com.sing4u.kr.common.dto.ResponseResult;
import com.sing4u.kr.common.enums.ResponseCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
    private final AuthService authService;

    @PostMapping("/login/email")
    public ResponseResult<LoginResponse> emailLogin(@RequestBody LoginRequest request,
                                                    HttpServletResponse response) {
        LoginTokenDto dto = authService.emailLogin(request);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", dto.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/api/v1/auth/reissue")
                .maxAge(Duration.ofDays(7))
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        return new ResponseResult<>(ResponseCode.SUCCESS, LoginResponse.of(dto.getAccessToken()));
    }

}
