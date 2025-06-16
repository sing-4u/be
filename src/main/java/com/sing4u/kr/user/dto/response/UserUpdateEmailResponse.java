package com.sing4u.kr.user.dto.response;

import com.sing4u.kr.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserUpdateEmailResponse {
    @Schema(description = "성공적으로 변경된 이메일 주소", example = "new_email@example.com")
    private String email;

    public static UserUpdateEmailResponse from(User user) {
        return UserUpdateEmailResponse.builder()
                .email(user.getEmail())
                .build();
    }
}
