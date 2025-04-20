package com.sing4u.kr.user.dto.response;

import com.sing4u.kr.user.entity.User;
import com.sing4u.kr.user.entity.enums.UserType;
import com.sing4u.kr.user.entity.enums.ActivityPlatformType;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private Long id;
    private String nickname;
    private String email;
    private UserType userType;
    private String profileImage;
    private String introduction;
    private String mainCoverUrl;
    private String activityPlatformUrl;
    private ActivityPlatformType activityPlatformType;
    private LocalDateTime updatedAt;

    public static UserProfileResponse from(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .userType(user.getUserType())
                .profileImage(user.getProfileImage())
                .introduction(user.getIntroduction())
                .mainCoverUrl(user.getMainCoverUrl())
                .activityPlatformUrl(user.getActivityPlatformUrl())
                .activityPlatformType(user.getActivityPlatformType())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
