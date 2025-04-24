package com.sing4u.kr.user.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserUpdateProfileRequest {
    private String profileImage;
    private String nickname;
    private String introduction;
    private String mainCoverUrl;
    private String activityPlatformUrl;
    private List<ActivityPlatformRequest> activityPlatforms;
}
