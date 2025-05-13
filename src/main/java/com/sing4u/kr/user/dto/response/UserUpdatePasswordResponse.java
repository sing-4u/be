package com.sing4u.kr.user.dto.response;

import com.sing4u.kr.user.entity.User;
import lombok.*;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserUpdatePasswordResponse {
    private String password;
    private String newPassword;

    public static UserUpdatePasswordResponse from(User user) {
        return UserUpdatePasswordResponse.builder()
                .password(user.getPassword())
                .newPassword(user.getPassword())
                .build();
    }
}
