package com.sing4u.kr.user.application.command;

import com.sing4u.kr.common.security.JwtTokenProvider;
import com.sing4u.kr.user.application.dto.UserCommand;
import com.sing4u.kr.user.domain.SignupType;
import com.sing4u.kr.user.domain.User;
import com.sing4u.kr.user.domain.UserRepository;
import com.sing4u.kr.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SignupUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public String execute(UserCommand command) {
        if (userRepository.findByEmail(command.email()).isPresent()) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(command.password());

        User user = new User(
                UUID.randomUUID(),
                command.email(),
                command.nickname(),
                encodedPassword,
                SignupType.LOCAL,
                UserRole.USER
        );

        userRepository.save(user);

        return jwtTokenProvider.generateToken(user.getEmail());
    }
}
