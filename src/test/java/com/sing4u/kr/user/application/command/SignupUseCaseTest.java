package com.sing4u.kr.user.application.command;

import com.sing4u.kr.common.security.JwtTokenProvider;
import com.sing4u.kr.user.application.dto.UserCommand;
import com.sing4u.kr.user.domain.OldUser;
import com.sing4u.kr.user.domain.OldUserRepository;
import com.sing4u.kr.user.domain.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class SignupUseCaseTest {

    @Mock
    OldUserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    SignupUseCase signupUseCase;

    @Test
    void 정상적으로_회원가입에_성공한다() {
        // given
        UUID uuid = UUID.randomUUID();
        String email = "test@example.com";
        String nickname = "tester";
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword";
        String token = "access.jwt.token";

        UserCommand command = new UserCommand(uuid, nickname, email, rawPassword, UserRole.USER);

        // findByEmail → 중복 X
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        // password 인코딩
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        // 토큰 생성
        when(jwtTokenProvider.generateToken(email)).thenReturn(token);

        // when
        String result = signupUseCase.execute(command);

        // then
        assertThat(result).isEqualTo(token);
        verify(userRepository).save(any(OldUser.class));
        verify(passwordEncoder).encode(rawPassword);
        verify(jwtTokenProvider).generateToken(email);
    }

    @Test
    void 이미_가입된_이메일이면_예외를_던진다() {
        // given
        UUID uuid = UUID.randomUUID();
        String email = "test@example.com";
        String nickname = "tester";
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword";
        String token = "access.jwt.token";
        UserCommand command = new UserCommand(uuid, nickname, email, rawPassword, UserRole.USER);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mock(OldUser.class)));

        // when & then
        assertThatThrownBy(() -> signupUseCase.execute(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 가입된 이메일입니다.");

        verify(userRepository, never()).save(any());
    }
}
