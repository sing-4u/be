package com.sing4u.kr.user.dto.request;

import com.sing4u.kr.user.entity.enums.ActivityPlatformType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateProfileRequest {
    private String profileImage;
    private String nickname;
    private String introduction;
    private String mainCoverUrl;
    private String activityPlatformUrl;
    private ActivityPlatformType activityPlatformType;
}
