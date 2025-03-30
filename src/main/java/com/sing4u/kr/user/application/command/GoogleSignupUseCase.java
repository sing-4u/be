package com.sing4u.kr.user.application.command;

import com.sing4u.kr.common.security.JwtTokenProvider;
import com.sing4u.kr.user.domain.SignupType;
import com.sing4u.kr.user.domain.User;
import com.sing4u.kr.user.domain.UserRepository;
import com.sing4u.kr.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GoogleSignupUseCase {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public String execute(String email, String nickname) {
        if (userRepository.findByEmail(email).isEmpty()) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }

        User user = new User(
                UUID.randomUUID(),
                email,
                nickname,
                null,
                SignupType.GOOGLE,
                UserRole.USER
        );

        userRepository.save(user);

        return jwtTokenProvider.generateToken(user.getEmail());
    }
}

