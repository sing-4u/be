package com.sing4u.kr.user.domain;

import com.sing4u.kr.user.infra.GoogleOAuthUserInfo;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public class OAuthUserMapper {
    public GoogleOAuthUserInfo fromGoogle(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        return new GoogleOAuthUserInfo(
                (String) attributes.get("sub"),
                (String) attributes.get("email"),
                (String) attributes.get("name"),
                (String) attributes.get("picture")
        );
    }
}
