package com.sing4u.kr.user.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserUpdateActivityPlatformRequest {
    @NotEmpty(message = "활동 플랫폼 리스트를 입력해주세요.")
    @Size(min = 1, message = "최소 1개 이상의 활동 플랫폼이 필요합니다.")
    private List<ActivityPlatformRequest> activityPlatforms;
}
