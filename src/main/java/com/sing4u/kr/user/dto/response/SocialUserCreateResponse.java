package com.sing4u.kr.user.dto.response;

import com.sing4u.kr.user.entity.User;
import com.sing4u.kr.user.entity.enums.SocialType;
import com.sing4u.kr.user.entity.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialUserCreateResponse {

    @Schema(description = "생성된 사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "사용자 이메일", example = "test@example.com")
    private String email;

    @Schema(description = "사용자 닉네임", example = "소셜유저")
    private String nickname;

    @Schema(description = "계정 유형", example = "USER", allowableValues = {"USER", "ARTIST"})
    private UserType userType;

    @Schema(description = "소셜 로그인 유형", example = "GOOGLE", allowableValues = {"GOOGLE", "LOCAL"})
    private SocialType socialType;

    @Schema(description = "가입 일시", example = "2025-06-25T10:00:00")
    private LocalDateTime createdAt;

    public static SocialUserCreateResponse from(User user) {
        return SocialUserCreateResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .userType(user.getUserType())
                .socialType(user.getSocialType())
                .createdAt(user.getCreatedAt())
                .build();
    }

}
