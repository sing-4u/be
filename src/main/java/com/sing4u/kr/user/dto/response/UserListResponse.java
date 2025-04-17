package com.sing4u.kr.user.dto.response;

import com.sing4u.kr.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserListResponse {
    private Long id;
    private String nickname;
    private String profileImage;

    public static UserListResponse from(User user) {
        return UserListResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .build();
    }
}
