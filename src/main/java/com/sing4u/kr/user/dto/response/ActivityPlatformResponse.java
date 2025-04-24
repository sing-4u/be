package com.sing4u.kr.user.dto.response;

import com.sing4u.kr.user.entity.UserActivityPlatform;
import com.sing4u.kr.user.entity.enums.ActivityPlatformType;
import lombok.*;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ActivityPlatformResponse {
    private ActivityPlatformType platformType;
    private String platformUrl;

    public static ActivityPlatformResponse from(UserActivityPlatform platform) {
        return new ActivityPlatformResponse(platform.getActivityPlatformType(), platform.getActivityPlatformUrl());
    }
}
