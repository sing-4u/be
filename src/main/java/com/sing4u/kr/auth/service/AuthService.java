package com.sing4u.kr.auth.service;

import com.sing4u.kr.auth.entity.RefreshToken;
import com.sing4u.kr.auth.dto.LoginDto;
import com.sing4u.kr.auth.dto.request.LoginRequest;
import com.sing4u.kr.auth.dto.response.TokenDto;
import com.sing4u.kr.auth.repository.RefreshTokenRepository;
import com.sing4u.kr.common.enums.ResponseCode;
import com.sing4u.kr.common.exception.Exception400;
import com.sing4u.kr.jwt.exceptions.InvalidTokenException;
import com.sing4u.kr.jwt.model.JwtToken;
import com.sing4u.kr.jwt.provider.JwtTokenProvider;
import com.sing4u.kr.user.entity.User;
import com.sing4u.kr.user.entity.enums.UserType;
import com.sing4u.kr.user.enums.UserRole;
import com.sing4u.kr.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public LoginDto emailLogin(LoginRequest request) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(() -> new Exception400(ResponseCode.ERROR_USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new Exception400(ResponseCode.ERROR_WRONG_PARAMETERS);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(),
                List.of(user.getRole()),
                user.getNickname()
        );

        String refreshTokenValue = jwtTokenProvider.generateRefreshToken(
                user.getId(),
                List.of(user.getRole()),
                user.getNickname()
        );

        refreshTokenRepository.save(RefreshToken.of(user.getId(), refreshTokenValue));
        return LoginDto.of(accessToken, refreshTokenValue, user.getProfileImage());
    }

    @Transactional
    public TokenDto refresh(String refreshToken) {
        // 전체적인 재발급 로직 변경 (Refresh Token Rotaion 적용)
        // 1. 요청으로 받은 Refresh Token 유효성 검증
        JwtToken jwt;
        try {
            jwt = jwtTokenProvider.getAllClaimsFromToken(refreshToken);
        } catch (Exception e) {
            throw new InvalidTokenException();
        }

        // 2. DB에서 해당 Refresh Token 조회
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> {
                    // DB에 토큰이 없다는 것은 이미 사용되었거나 탈취 후 삭제되었을 가능성이 있음
                    // 로그를 남기고 예외 처리
                    log.warn("Invalid or already used refresh token: {}", refreshToken);
                    return new InvalidTokenException();
                });

        // 3. 기존 Refresh Token을 DB에서 삭제 (재사용 방지)
        refreshTokenRepository.deleteByToken(refreshToken);

        // 4. 새로운 Access Token과 Refresh Token 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(
                jwt.getUserId(),
                jwt.getRoles(),
                jwt.getNickName()
        );
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(
                jwt.getUserId(),
                jwt.getRoles(),
                jwt.getNickName()
        );

        // 5. 새로 생성된 Refresh Token을 DB에 저장
        refreshTokenRepository.save(RefreshToken.of(jwt.getUserId(), newRefreshToken));

        return TokenDto.of(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}
