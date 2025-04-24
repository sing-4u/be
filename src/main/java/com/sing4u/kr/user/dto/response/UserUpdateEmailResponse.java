package com.sing4u.kr.user.dto.response;

import com.sing4u.kr.user.entity.User;
import lombok.*;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserUpdateEmailResponse {
    private String email;

    public static UserUpdateEmailResponse from(User user) {
        return UserUpdateEmailResponse.builder()
                .email(user.getEmail())
                .build();
    }
}
