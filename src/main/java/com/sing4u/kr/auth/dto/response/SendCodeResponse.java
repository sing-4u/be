package com.sing4u.kr.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프론트 타이머/마스킹 표시용 응답 DTO
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SendCodeResponse {
    // 인증번호 유효 시간(초) — 기본 180
    private int expiresIn;

    // 재전송 가능 대기 시간(초) — 기본 30
    private int resendAvailableIn;

    // 마스킹된 이메일
    private String maskedEmail;
}
