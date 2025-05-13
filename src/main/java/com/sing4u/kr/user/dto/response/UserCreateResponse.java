package com.sing4u.kr.user.dto.response;

import com.sing4u.kr.user.entity.User;
import com.sing4u.kr.user.entity.enums.UserType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCreateResponse {
    private Long userId;
    private String email;
    private String nickname;
    private UserType userType;
    private LocalDateTime createdAt;

    public static UserCreateResponse from(User user) {
        return UserCreateResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .userType(user.getUserType())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
