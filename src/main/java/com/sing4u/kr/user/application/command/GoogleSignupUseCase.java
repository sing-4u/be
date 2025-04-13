package com.sing4u.kr.user.application.command;

import com.sing4u.kr.common.security.JwtTokenProvider;
import com.sing4u.kr.user.domain.SignupType;
import com.sing4u.kr.user.domain.OldUser;
import com.sing4u.kr.user.domain.OldUserRepository;
import com.sing4u.kr.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GoogleSignupUseCase {
    private final OldUserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public String execute(String email, String nickname) {
        if (userRepository.findByEmail(email).isEmpty()) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }

        OldUser user = new OldUser(
                UUID.randomUUID(),
                email,
                nickname,
                null,
                SignupType.GOOGLE,
                UserRole.USER
        );

        userRepository.save(user);

        return null;
    }
}

