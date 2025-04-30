package com.sing4u.kr.health.dto;

import lombok.*;


import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SwaggerAuthResponse {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_toke")
    private String refreshToken;
}
