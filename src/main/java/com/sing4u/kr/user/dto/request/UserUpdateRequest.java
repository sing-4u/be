package com.sing4u.kr.user.dto.request;

import com.sing4u.kr.user.entity.enums.AccountType;
import com.sing4u.kr.user.entity.enums.ActivityPlatformType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private String nickname;
    private String email;
    private String password;
    private String newPassword;
    private AccountType accountType;
    private String profileImage;
    private String introduction;
    private String activityPlatformUrl;
    private ActivityPlatformType activityPlatformType;
}
