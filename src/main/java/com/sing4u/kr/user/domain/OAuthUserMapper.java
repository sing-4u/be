package com.sing4u.kr.user.domain;

import com.sing4u.kr.user.infra.GoogleOAuthUserInfo;

public class OAuthUserMapper {
    public User toUser(GoogleOAuthUserInfo info) {
        return User.createGoogleUser(
        );
    }
}
