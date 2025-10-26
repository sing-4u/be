package com.sing4u.kr.auth.oauth.handler;

import com.sing4u.kr.auth.utils.CookieUtils;
import com.sing4u.kr.auth.dto.CustomUserPrincipal;
import com.sing4u.kr.auth.entity.RefreshToken;
import com.sing4u.kr.auth.repository.RefreshTokenRepository;
import com.sing4u.kr.jwt.provider.JwtTokenProvider;
import com.sing4u.kr.user.entity.enums.UserType;
import com.sing4u.kr.user.enums.UserRole;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
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
        String role = principal.getAuthorities().iterator().next().getAuthority();
        String nickname = principal.getNickname();

        // Access Token과 Refresh Token 생성 (userId 기반)
        List<UserRole> roles = List.of(UserRole.valueOf(role));
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
