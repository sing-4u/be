package com.sing4u.kr.user.application.command;

import com.sing4u.kr.user.domain.OldUser;
import com.sing4u.kr.user.domain.OldUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UpdateProfileImageUseCase {
    private final OldUserRepository userRepository;

    public void execute(UUID userId, String profileImageUrl) {
        OldUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.updateProfileImage(profileImageUrl);
    }
}
