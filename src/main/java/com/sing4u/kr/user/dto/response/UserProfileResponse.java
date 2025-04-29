package com.sing4u.kr.user.dto.response;

import com.sing4u.kr.user.entity.User;
import com.sing4u.kr.user.entity.enums.UserType;
import com.sing4u.kr.user.entity.UserActivityPlatform;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfileResponse {
    private Long userId;
    private String nickname;
    private String email;
    private UserType userType;
    private String profileImage;
    private String introduction;
    private String mainCoverUrl;
    private LocalDateTime updatedAt;
    private List<ActivityPlatformResponse> activityPlatforms;

    public static UserProfileResponse from(User user, List<UserActivityPlatform> platforms) {
        return UserProfileResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .userType(user.getUserType())
                .profileImage(user.getProfileImage())
                .introduction(user.getIntroduction())
                .mainCoverUrl(user.getMainCoverUrl())
                .updatedAt(user.getUpdatedAt())
                .activityPlatforms(platforms.stream()
                        .map(ActivityPlatformResponse::from)
                        .toList())
                .build();
    }
}
