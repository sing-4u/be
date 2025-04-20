package com.sing4u.kr.user.dto.response;

import com.sing4u.kr.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
