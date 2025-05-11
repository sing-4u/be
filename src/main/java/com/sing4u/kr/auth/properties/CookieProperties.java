package com.sing4u.kr.auth.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cookie")
public class CookieProperties {
    private String refreshName;
    private String refreshPath;
    private boolean refreshHttpOnly;
    private boolean refreshSecure;
    private String refreshSameSite;
    private int refreshMaxAgeDays;
}
