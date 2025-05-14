package com.sing4u.kr.user.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserFileUtils {

    public String getProfileImageKey(Long userId) {
        return String.format("user/%d/profile", userId);
    }
}
