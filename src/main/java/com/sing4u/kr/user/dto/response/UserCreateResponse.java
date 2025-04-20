package com.sing4u.kr.user.dto.response;

import com.sing4u.kr.user.entity.User;
import com.sing4u.kr.user.entity.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateResponse {
    private Long id;
    private String email;
    private String nickname;
    private UserType userType;
    private LocalDateTime createdAt;

    public static UserCreateResponse from(User user) {
        return UserCreateResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .userType(user.getUserType())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
