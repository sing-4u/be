package com.sing4u.kr.user.infra;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class GoogleOAuthClient {

    private final WebClient webClient = WebClient.create();

    public GoogleOAuthUserInfo getUserInfo(String accessToken) {
        return webClient.get()
                .uri("https://www.googleapis.com/oauth2/v2/userinfo")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(GoogleOAuthUserInfo.class)
                .block();
    }
}
