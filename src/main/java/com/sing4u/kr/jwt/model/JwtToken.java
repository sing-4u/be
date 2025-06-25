package com.sing4u.kr.jwt.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.util.Date;
import java.util.List;

import com.sing4u.kr.user.entity.enums.UserType;
import com.sing4u.kr.user.enums.UserRole;

@Getter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JwtToken {
    private Long userId;
    private Date expiredAt;
    private List<UserRole> roles;
    private String nickName;
    private String userName;
}
