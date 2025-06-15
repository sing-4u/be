package com.sing4u.kr.user.dto.response;

import com.sing4u.kr.user.entity.User;
import lombok.*;

import java.util.List;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserListResponse {
    private Long userId;
    private String nickname;
    private String profileImage;
    private boolean isOpen;

    public static UserListResponse from(User user) {
        return UserListResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .isOpen(user.isOpen())
                .build();
    }

    public static List<UserListResponse> fromList(List<User> users) {
        return users.stream()
                .map(UserListResponse::from)
                .toList();
    }
}
