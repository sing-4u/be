package com.sing4u.kr.user.dto.request;

import com.sing4u.kr.user.entity.enums.ActivityPlatformType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
public class ActivityPlatformRequest {

    @Schema(description = "플랫폼 타입", example = "YOUTUBE", allowableValues = {"YOUTUBE", "INSTAGRAM", "TIKTOK", "SOUNDCLOUD"})
    private ActivityPlatformType platformType;

    @Schema(description = "플랫폼 URL 주소", example = "https://www.youtube.com/channel/UC-abcdefg")
    private String platformUrl;
}
