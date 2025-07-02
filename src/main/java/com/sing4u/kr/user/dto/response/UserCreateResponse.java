package com.sing4u.kr.user.dto.response;

import com.sing4u.kr.user.entity.User;
import com.sing4u.kr.user.entity.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCreateResponse {

    @Schema(description = "생성된 사용자 공개 ID", example = "cd7f1081-e380-4fc7-bb7e-23cf4d7b6d44")
    private String userPublicId;

    @Schema(description = "사용자 이메일", example = "test@example.com")
    private String email;

    @Schema(description = "사용자 닉네임", example = "테스트유저")
    private String nickname;

    @Schema(description = "계정 유형", example = "FAN", implementation = UserType.class)
    private UserType userType;

    @Schema(description = "가입 일시", example = "2025-06-16T14:00:00")
    private LocalDateTime createdAt;
    public static UserCreateResponse from(User user) {
        return UserCreateResponse.builder()
                .userPublicId(user.getUserPublicId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .userType(user.getUserType())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
