package com.sing4u.kr.auth.oauth.handler;

import com.sing4u.kr.auth.utils.CookieUtils;
import com.sing4u.kr.auth.dto.CustomUserPrincipal;
import com.sing4u.kr.auth.entity.RefreshToken;
import com.sing4u.kr.auth.repository.RefreshTokenRepository;
import com.sing4u.kr.jwt.provider.JwtTokenProvider;
import com.sing4u.kr.user.enums.UserRole; // 이 경로는 그대로 유지
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${sing4u.oauth-redirect-url}")
    private String redirectUrl;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        Long userId = principal.getUserId();

        // 1. 권한 목록에서 첫 번째 권한 문자열을 추출 (없으면 "USER" 기본값)
        String authority = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER"); // Spring Security 포맷을 따라 기본값도 ROLE_ 붙임

        // 2. "ROLE_" 접두사 제거. 예: "ROLE_USER" -> "USER"
        String enumName = authority.startsWith("ROLE_") ? authority.substring(5) : authority;
        UserRole userRole;

        try {
            // 3. Enum으로 변환 시도
            userRole = UserRole.valueOf(enumName);
        } catch (IllegalArgumentException e) {
            // 4. 변환 실패 시 예외 처리 및 로그 기록 후, 기본 역할(USER) 할당
            log.warn("Unknown role mapping: {}. Falling back to USER.", authority);
            userRole = UserRole.USER;
        }

        String nickname = principal.getNickname();

        // 최종적으로 안전하게 변환된 userRole 사용
        List<UserRole> roles = List.of(userRole);

        // Access Token과 Refresh Token 생성 (userId 기반)
        String accessToken = jwtTokenProvider.generateAccessToken(userId, roles, nickname);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userId, roles, nickname);

        // Refresh Token을 DB에 저장
        refreshTokenRepository.save(RefreshToken.of(userId, refreshToken));

        // Access Token은 쿠키에 담아서 프론트로 전달 (토큰 교환용)
        CookieUtils.setAccessTokenCookie(response, accessToken);
        // Refresh Token도 별도 쿠키로 전달 (재발급용)
        CookieUtils.setRefreshTokenCookie(response, refreshToken);

        // 프론트의 특정 콜백 URL로 리디렉션
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}