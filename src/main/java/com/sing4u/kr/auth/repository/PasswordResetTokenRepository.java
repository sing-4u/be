package com.sing4u.kr.auth.repository;

public interface PasswordResetTokenRepository {
    // OTP 코드 (3분)
    void saveCode(String email, String code);
    String getCode(String email);
    void removeCode(String email);

    // 30초 재요청 제한
    boolean isThrottled(String email);
    void throttle(String email);
    // 쿨다운 강제 해제
    void clearThrottle(String email);

    // 리셋 토큰 (10분)
    void saveTicket(String email, String ticket);
    String getTicket(String email);
    void removeTicket(String email);
}
