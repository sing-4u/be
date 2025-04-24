package com.sing4u.kr.user.dto;

import lombok.*;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDto {
    private Long userId;
    private String email;
    private String nickname;
    private String password;
    private String newPassword;
}
