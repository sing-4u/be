package com.sing4u.kr.auth.mail;

import com.sing4u.kr.auth.event.PasswordResetCodeIssuedEvent;
import com.sing4u.kr.auth.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordResetMailerListener {

    private final PasswordResetMailer mailer;
    private final PasswordResetTokenRepository passwordResetTokens;

    @Async("mailExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(PasswordResetCodeIssuedEvent e) {
        try {
            mailer.send(e.email(), e.code());
        } catch (Exception ex) {
            log.error("Password reset mail send failed. email={}, err={}", e.email(), ex.toString(), ex);
            // 실패 시: 사용자가 바로 재전송 가능하도록 복구
            passwordResetTokens.removeCode(e.email());     // 발급된 인증코드 무효화
            passwordResetTokens.clearThrottle(e.email());  // 30초 쿨다운 즉시 해제
            log.info("[MAIL][LISTENER] rollback cleared code & throttle for {}", e.email());
        }
    }
}
