package com.sing4u.kr.user.dto.response;

import com.sing4u.kr.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserUpdatePasswordResponse {
    @Schema(description = "이전 비밀번호 (보안상 응답에 포함하지 않는 것을 권장)", example = "password123!")
    private String password;

    @Schema(description = "새로운 비밀번호 (보안상 응답에 포함하지 않는 것을 권장)", example = "new_password456!")
    private String newPassword;
    public static UserUpdatePasswordResponse from(User user) {
        return UserUpdatePasswordResponse.builder()
                .currentPassword(user.getPassword())
                .newPassword(user.getPassword())
                .build();
    }
}
