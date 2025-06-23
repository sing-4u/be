package com.sing4u.kr.user.dto.request;

import com.sing4u.kr.user.entity.enums.ActivityPlatformType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateProfileRequest {
    @Schema(description = "프로필 이미지 URL", example = "https://example.com/new_profile.jpg")
    private String profileImage;

    @Schema(description = "새 닉네임", example = "새로운닉네임")
    private String nickname;

    @Schema(description = "자기소개", example = "안녕하세요. 반갑습니다.")
    private String introduction;

    @Schema(description = "메인 커버 이미지 URL", example = "https://example.com/new_cover.jpg")
    private String mainCoverUrl;

    @Schema(description = "활동 플랫폼 목록 (프로필 수정 시 함께 변경 가능)", implementation = ActivityPlatformRequest.class)
    private List<ActivityPlatformRequest> activityPlatforms;
}
