package com.sing4u.kr.user.application.command;

import com.sing4u.kr.common.security.JwtTokenProvider;
import com.sing4u.kr.user.application.dto.TokenResponse;
import com.sing4u.kr.user.application.dto.UserCommand;
import com.sing4u.kr.user.domain.SignupType;
import com.sing4u.kr.user.domain.OldUser;
import com.sing4u.kr.user.domain.OldUserRepository;
import com.sing4u.kr.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SignupUseCase {
    private final OldUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public TokenResponse execute(UserCommand command) {
        if (userRepository.findByEmail(command.email()).isPresent()) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(command.password());

        OldUser user = new OldUser(
                UUID.randomUUID(),
                command.email(),
                command.nickname(),
                encodedPassword,
                SignupType.LOCAL,
                UserRole.USER
        );

        userRepository.save(user);

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        return new TokenResponse(accessToken, refreshToken);
    }
}
