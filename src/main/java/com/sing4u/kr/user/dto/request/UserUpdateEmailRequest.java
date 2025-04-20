package com.sing4u.kr.user.dto.request;

import com.sing4u.kr.user.entity.enums.SocialType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateEmailRequest {
    private String email;
    private String password;
    private SocialType socialType;
}
