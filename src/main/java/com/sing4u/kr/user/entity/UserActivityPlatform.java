package com.sing4u.kr.user.entity;

import com.sing4u.kr.user.entity.enums.ActivityPlatformType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserActivityPlatform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long platformId;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_platform_type", length = 20, nullable = false)
    private ActivityPlatformType activityPlatformType;

    @Column(name = "activity_platform_url", length = 255, nullable = false)
    private String activityPlatformUrl;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    public static UserActivityPlatform of(ActivityPlatformType activityPlatformType, String activityPlatformUrl, Long userId) {
        return UserActivityPlatform.builder()
                .activityPlatformType(activityPlatformType)
                .activityPlatformUrl(activityPlatformUrl)
                .userId(userId)
                .build();
    }
}
