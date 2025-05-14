package com.sing4u.kr.user.dto.response;

import lombok.*;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserUpdateProfileImageResponse {
    private String profileImage;

    public static UserUpdateProfileImageResponse of(String profileImage) {
        return UserUpdateProfileImageResponse.builder()
                .profileImage(profileImage)
                .build();
    }
}
