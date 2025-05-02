package com.sing4u.kr.auth.service;

import com.sing4u.kr.auth.Entity.RefreshToken;
import com.sing4u.kr.auth.dto.LoginTokenDto;
import com.sing4u.kr.auth.dto.request.LoginRequest;
import com.sing4u.kr.auth.dto.response.LoginResponse;
import com.sing4u.kr.auth.repository.RefreshTokenRepository;
import com.sing4u.kr.common.enums.ResponseCode;
import com.sing4u.kr.common.exception.Exception400;
import com.sing4u.kr.jwt.provider.JwtTokenProvider;
import com.sing4u.kr.user.entity.User;
import com.sing4u.kr.user.enums.UserRole;
import com.sing4u.kr.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public LoginTokenDto emailLogin(LoginRequest request) {
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

        String refreshTokenVal = jwtTokenProvider.generateRefreshToken(user.getEmail());
        RefreshToken refreshToken = RefreshToken.of(user.getId(), refreshTokenVal);
        refreshTokenRepository.save(refreshToken);
        return LoginTokenDto.of(accessToken, refreshTokenVal);
    }
}
