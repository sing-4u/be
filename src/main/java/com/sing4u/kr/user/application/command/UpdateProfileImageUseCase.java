package com.sing4u.kr.user.application.command;

import com.sing4u.kr.user.domain.User;
import com.sing4u.kr.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UpdateProfileImageUseCase {
    private final UserRepository userRepository;

    public void execute(UUID userId, String profileImageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.updateProfileImage(profileImageUrl);
    }
}
