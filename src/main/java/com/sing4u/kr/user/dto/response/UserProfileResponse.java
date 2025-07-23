package com.sing4u.kr.user.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sing4u.kr.user.entity.User;
import com.sing4u.kr.user.entity.enums.SocialType;
import com.sing4u.kr.user.entity.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfileResponse {

    @Schema(description = "생성된 사용자 공개 ID", example = "cd7f1081-e380-4fc7-bb7e-23cf4d7b6d44")
    private String userPublicId;

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

    @JsonProperty("isOpen")
    @Schema(description = "세션 오픈 여부", example = "false")
    private boolean isOpen;

    @Schema(description = "소셜 계정 유형", example = "GOOGLE", implementation = SocialType.class)
    private SocialType socialType;

    @Schema(description = "마지막 수정 일시", example = "2025-06-16T15:00:00")
    private LocalDateTime updatedAt;

    public static UserProfileResponse from(User user) {
        return UserProfileResponse.builder()
                .userPublicId(user.getUserPublicId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .userType(user.getUserType())
                .profileImage(user.getProfileImage())
                .introduction(user.getIntroduction())
                .mainCoverUrl(user.getMainCoverUrl())
                .updatedAt(user.getUpdatedAt())
                .isOpen(user.isOpen())
                .socialType(user.getSocialType())
                .build();
    }

    @JsonIgnore  // Lombok이 만든 isOpen() 무시
    public boolean isOpen() {
        return isOpen;
    }
}
