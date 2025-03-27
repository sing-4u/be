package com.sing4u.kr.user.application.dto;

import com.sing4u.kr.user.domain.User;
import com.sing4u.kr.user.domain.UserRole;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String nickname,
        String email,
        UserRole userRole,
        String profileImageUrl
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                user.getUserRole(),
                user.getProfileImageUrl()
        );
    }
}

