package com.sing4u.kr.auth.config;

import com.sing4u.kr.auth.properties.CookieProperties;
import com.sing4u.kr.auth.utils.CookieUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookiePropertyInitializer {

    private final CookieProperties cookieProperties;
    @PostConstruct
    public void init() {
        CookieUtils.setCookieProperties(cookieProperties);
    }
}

