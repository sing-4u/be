package com.sing4u.kr.user.infra;

public record GoogleOAuthUserInfo(
        String id,
        String email,
        String name,
        String picture
) {}
