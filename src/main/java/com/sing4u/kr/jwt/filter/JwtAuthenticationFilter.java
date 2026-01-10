package com.sing4u.kr.jwt.filter;

import com.sing4u.kr.auth.utils.CookieUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import com.sing4u.kr.application.utils.SecurityContextUtils;
import com.sing4u.kr.jwt.exceptions.ExpiredTokenException;
import com.sing4u.kr.jwt.exceptions.InvalidTokenException;
import com.sing4u.kr.jwt.model.JwtToken;
import com.sing4u.kr.jwt.provider.JwtTokenProvider;

import static com.sing4u.kr.common.enums.ResponseCode.ERROR_EXPIRED_TOKEN;
import static com.sing4u.kr.common.enums.ResponseCode.ERROR_INVALID_TOKEN;
import static com.sing4u.kr.common.properties.GlobalProperties.HEADER_EXCEPTION_CODE;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (path.startsWith("/api/v1/auth/login")) {
            filterChain.doFilter(request, response);
            return;  // 로그인 요청은 토큰 검사 스킵
        }

        String accessToken =  CookieUtils.extractAccessTokenFromCookie(request);

        if (StringUtils.isBlank(accessToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            JwtToken token = jwtTokenProvider.getAllClaimsFromToken(accessToken);
            SecurityContextUtils.setSecurityContext(token.getUserId(), token.getEmail(), token.getRoles());
        } catch (ExpiredJwtException e) {
            response.setHeader(HEADER_EXCEPTION_CODE, ERROR_EXPIRED_TOKEN.getCode());
            throw new ExpiredTokenException();
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            response.setHeader(HEADER_EXCEPTION_CODE, ERROR_INVALID_TOKEN.getCode());
            log.error("invalid token exception, request uri : {}, accessToken : {}", request.getRequestURI(), accessToken);
            throw new InvalidTokenException(e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

}
