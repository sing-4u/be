package com.sing4u.kr.auth.service;

import com.sing4u.kr.auth.Entity.RefreshToken;
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
import com.sing4u.kr.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        User user = userRepository.findByEmailDeletedAtisNull(request.getEmail())
                .orElseThrow(() -> new Exception400("사용자를 찾을 수 없습니다.", ResponseCode.ERROR_NO_DATA));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new Exception400("비밀번호가 일치하지 않습니다.", ResponseCode.ERROR_WRONG_PARAMETERS);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(),
                List.of(user.getRole()),
                user.getNickname(),
                user.getUserType()
        );

        String refreshTokenValue = jwtTokenProvider.generateRefreshToken(
                user.getId(),
                List.of(user.getRole()),
                user.getNickname(),
                user.getUserType()
        );

        refreshTokenRepository.save(RefreshToken.of(user.getId(), refreshTokenValue));
        return LoginDto.of(accessToken, refreshTokenValue, user.getProfileImage());
    }

    @Transactional
    public TokenDto recreate(String refreshToken) {
        JwtToken jwt;
        try {
            jwt = jwtTokenProvider.getAllClaimsFromToken(refreshToken);
        } catch (Exception e) {
            throw new InvalidTokenException();
        }

        RefreshToken storedToken = refreshTokenRepository.findById(jwt.getUserId())
                .orElseThrow(InvalidTokenException::new);

        String newRefreshToken = refreshToken;
        if (jwtTokenProvider.isTokenExpired(refreshToken)) {
            newRefreshToken = jwtTokenProvider.generateRefreshToken(
                    jwt.getUserId(),
                    jwt.getRoles(),
                    jwt.getNickName(),
                    jwt.getUserType()
            );
            storedToken.update(newRefreshToken);
            refreshTokenRepository.save(storedToken);
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(
                jwt.getUserId(),
                jwt.getRoles(),
                jwt.getNickName(),
                jwt.getUserType()
        );

        return TokenDto.of(newAccessToken, newRefreshToken);
    }
}
