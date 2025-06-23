package com.sing4u.kr.user.dto.response;

import com.sing4u.kr.user.entity.User;
import com.sing4u.kr.user.entity.enums.UserType;
import com.sing4u.kr.user.entity.UserActivityPlatform;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfileResponse {
    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "닉네임", example = "노래하는강아지")
    private String nickname;

    @Schema(description = "이메일", example = "artist@test.com")
    private String email;

    @Schema(description = "계정 유형", example = "ARTIST", implementation = UserType.class)
    private UserType userType;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile2.jpg")
    private String profileImage;

    @Schema(description = "자기소개", example = "저는 노래를 만드는 아티스트입니다.")
    private String introduction;

    @Schema(description = "메인 커버 이미지 URL", example = "https://example.com/cover2.jpg")
    private String mainCoverUrl;

    @Schema(description = "마지막 수정 일시", example = "2025-06-16T15:00:00")
    private LocalDateTime updatedAt;
    public static UserProfileResponse from(User user) {
        return UserProfileResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .userType(user.getUserType())
                .profileImage(user.getProfileImage())
                .introduction(user.getIntroduction())
                .mainCoverUrl(user.getMainCoverUrl())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
