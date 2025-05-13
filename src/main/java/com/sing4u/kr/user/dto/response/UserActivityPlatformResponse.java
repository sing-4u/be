package com.sing4u.kr.user.dto.response;

import com.sing4u.kr.user.entity.UserActivityPlatform;
import lombok.*;

import java.util.List;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserActivityPlatformResponse {
    private List<ActivityPlatformResponse> activityPlatforms;

    public static UserActivityPlatformResponse from(List<UserActivityPlatform> platforms) {
        return UserActivityPlatformResponse.builder()
                .activityPlatforms(platforms.stream()
                        .map(ActivityPlatformResponse::from)
                        .toList())
                .build();
    }
}
