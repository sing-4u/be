package com.sing4u.kr.user.application.command;

import com.sing4u.kr.common.security.JwtTokenProvider;
import com.sing4u.kr.user.domain.OldUser;
import com.sing4u.kr.user.domain.OldUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoogleLoginUseCase {

    private final OldUserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public String execute(String email) {
        OldUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return null;
    }
}
