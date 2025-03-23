package com.sing4u.kr.user.application.command;

import com.sing4u.kr.user.application.dto.UserCommand;
import com.sing4u.kr.user.application.dto.UserResponse;
import com.sing4u.kr.user.domain.User;
import com.sing4u.kr.user.domain.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateUserUseCase {
    private final UserRepository userRepository;

    @Transactional
    public UserResponse execute(UserCommand userCommand) {
        User user = User.create();
        return UserResponse.from(userRepository.save(user));
    }
}
