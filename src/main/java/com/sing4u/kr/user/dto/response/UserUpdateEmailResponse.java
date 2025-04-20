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
public class UserUpdateEmailResponse {
    private String email;

    public static UserUpdateEmailResponse from(User user) {
        return UserUpdateEmailResponse.builder()
                .email(user.getEmail())
                .build();
    }
}
