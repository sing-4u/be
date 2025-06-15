package com.sing4u.kr.user.dto.request;

import com.sing4u.kr.user.entity.enums.ActivityPlatformType;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
public class ActivityPlatformRequest {
    private ActivityPlatformType platformType;
    private String platformUrl;
}
