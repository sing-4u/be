package com.sing4u.kr.user.dto.response;

import com.sing4u.kr.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserListResponse {
    @Schema(description = "생성된 사용자 공개 ID", example = "cd7f1081-e380-4fc7-bb7e-23cf4d7b6d44")
    private String publicId;

    @Schema(description = "닉네임", example = "검색된유저")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile10.jpg")
    private String profileImage;
    private boolean isOpen;

    public static UserListResponse from(User user) {
        return UserListResponse.builder()
                .publicId(user.getPublicId())
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
