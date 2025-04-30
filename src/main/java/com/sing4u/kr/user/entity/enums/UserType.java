package com.sing4u.kr.user.entity.enums;

import org.apache.commons.lang3.StringUtils;

public enum UserType {
    USER, ARTIST;

    public static UserType toUserType(String userType) {
        for (UserType type : UserType.values()) {
            if(StringUtils.equals(userType, type.toString())) {
                return type;
            }
        }

        return null;
    }
}
