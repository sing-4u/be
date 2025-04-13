package com.sing4u.kr.user.application.command;

import com.sing4u.kr.common.security.JwtTokenProvider;
import com.sing4u.kr.user.application.dto.LoginCommand;
import com.sing4u.kr.user.application.dto.TokenResponse;
import com.sing4u.kr.user.domain.OldUser;
import com.sing4u.kr.user.domain.OldUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginUseCase {
    private final OldUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public TokenResponse execute(LoginCommand command) {
        OldUser user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (!user.isLocalUser()) {
            throw new IllegalStateException("소셜 로그인 사용자입니다.");
        }

        if (!passwordEncoder.matches(command.password(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return null;
    }
}
