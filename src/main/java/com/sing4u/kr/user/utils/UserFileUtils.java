package com.sing4u.kr.user.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserFileUtils {

    public String getProfileImageKey(Long userId) {
        return String.format("user/%d/profile", userId);
    }

    public static String extractS3KeyFromUrl(String fileUrl, String fullUrl) {
        if (fullUrl != null && fullUrl.startsWith(fileUrl)) {
            return fullUrl.replace(fileUrl, "");
        }
        return null;
    }
}
