package com.sing4u.kr.user.dto.response;

import com.sing4u.kr.user.entity.UserActivityPlatform;
import com.sing4u.kr.user.entity.enums.ActivityPlatformType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "활동 플랫폼 정보 응답 DTO")
public class ActivityPlatformResponse {

    @Schema(description = "플랫폼 타입", example = "YOUTUBE", implementation = ActivityPlatformType.class)
    private ActivityPlatformType platformType;

    @Schema(description = "플랫폼 URL", example = "https://www.youtube.com/channel/UC-abcdefg")
    private String platformUrl;

    public static ActivityPlatformResponse from(UserActivityPlatform platform) {
        return ActivityPlatformResponse.builder()
                .platformType(platform.getActivityPlatformType())
                .platformUrl(platform.getActivityPlatformUrl())
                .build();
    }
}
