package com.sing4u.kr.auth.utils;

import com.sing4u.kr.jwt.exceptions.InvalidTokenException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

@UtilityClass
public class CookieUtils {

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

    public static String extractRefreshTokenFromCookie(HttpServletRequest request) {
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

    public static void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
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
