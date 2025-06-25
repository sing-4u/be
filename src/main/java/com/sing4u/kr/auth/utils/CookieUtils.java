package com.sing4u.kr.auth.utils;

import com.sing4u.kr.auth.properties.CookieProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

@UtilityClass
public class CookieUtils {

    static CookieProperties cookieProperties;

    public static void setCookieProperties(CookieProperties properties) {
        cookieProperties = properties;
    }

    public String extractRefreshTokenFromCookie(HttpServletRequest request) {
        return extractTokenFromCookie(request, cookieProperties.getRefreshName());
    }

    public String extractAccessTokenFromCookie(HttpServletRequest request) {
        return extractTokenFromCookie(request, cookieProperties.getAccessName());
    }

    private String extractTokenFromCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(cookieProperties.getRefreshName(), refreshToken)
                .httpOnly(cookieProperties.isRefreshHttpOnly())
                .secure(cookieProperties.isRefreshSecure())
                .sameSite(cookieProperties.getRefreshSameSite())
                .path(cookieProperties.getRefreshPath())
                .maxAge(Duration.ofDays(cookieProperties.getRefreshMaxAgeDays()))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void setAccessTokenCookie(HttpServletResponse response, String accessToken) {
        ResponseCookie cookie = ResponseCookie.from(cookieProperties.getAccessName(), accessToken)
                .httpOnly(cookieProperties.isAccessHttpOnly())
                .secure(cookieProperties.isAccessSecure())
                .sameSite(cookieProperties.getAccessSameSite())
                .path(cookieProperties.getAccessPath())
                .maxAge(Duration.ofMinutes(cookieProperties.getAccessMaxAgeMinutes()))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
