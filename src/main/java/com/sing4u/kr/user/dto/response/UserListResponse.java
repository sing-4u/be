package com.sing4u.kr.user.dto.response;

import com.sing4u.kr.user.entity.User;
import lombok.*;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserListResponse {
    private Long userId;
    private String nickname;
    private String profileImage;

    public static UserListResponse from(User user) {
        return UserListResponse.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .build();
    }
}
