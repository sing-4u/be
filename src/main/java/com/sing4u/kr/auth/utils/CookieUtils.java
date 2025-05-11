package com.sing4u.kr.auth.utils;

import com.sing4u.kr.auth.properties.CookieProperties;
import com.sing4u.kr.jwt.exceptions.InvalidTokenException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

@UtilityClass
public class CookieUtils {

    static CookieProperties cookieProperties;

    public static String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieProperties.getRefreshName().equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        throw new InvalidTokenException();
    }

    public static void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(cookieProperties.getRefreshName(), refreshToken)
                .httpOnly(cookieProperties.isRefreshHttpOnly())
                .secure(cookieProperties.isRefreshSecure())
                .sameSite(cookieProperties.getRefreshSameSite())
                .path(cookieProperties.getRefreshPath())
                .maxAge(Duration.ofDays(cookieProperties.getRefreshMaxAgeDays()))
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
