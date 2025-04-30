package com.sing4u.kr.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum UserRole {

    USER("ROLE_USER"),
    ARTIST("ROLE_ARTIST"),
    ADMIN("ROLE_ADMIN");

    private final String jwtRole;

    public static UserRole getMainAccountRole(List<UserRole> roleList) {
        Set<UserRole> roleSet = new HashSet<>(roleList);

        if (roleSet.contains(UserRole.ADMIN)) {
            return UserRole.ADMIN;
        } else if (roleSet.contains(UserRole.ARTIST)) {
            return UserRole.ARTIST;
        } else if (roleSet.contains(UserRole.USER)) {
            return UserRole.USER;
        } else {
            return UserRole.USER;
        }
    }

    public static UserRole getMainAccountRoleFromString(List<String> roleList) {
        for(String role : roleList) {
            if (role.equals(UserRole.ADMIN.toString())) {
                return UserRole.ADMIN;
            } else if (role.equals(UserRole.ARTIST.toString())) {
                return UserRole.ARTIST;
            } else if (role.equals(UserRole.USER.toString())) {
                return UserRole.USER;
            }
        }
        return UserRole.USER;
    }

    public static Map<String, UserRole> getUserRoleStringMap() {
        return Stream.of(UserRole.values())
                .collect(Collectors.toMap(UserRole::toString, role -> role));
    }

    public static List<UserRole> getRoleByStringList(List<String> roleList) {
        Map<String, UserRole> userRoleMap = getUserRoleStringMap();

        return roleList.stream()
                .map(role -> userRoleMap.getOrDefault(role, null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
