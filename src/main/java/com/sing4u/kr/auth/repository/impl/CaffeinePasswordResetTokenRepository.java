package com.sing4u.kr.auth.repository.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.sing4u.kr.auth.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class CaffeinePasswordResetTokenRepository implements PasswordResetTokenRepository {
    private final Cache<String, String> otp;
    private final Cache<String, String> thr;
    private final Cache<String, String> tkt;

    public CaffeinePasswordResetTokenRepository(
            @Qualifier("otpCodeCache") Cache<String, String> otp,
            @Qualifier("throttleCache") Cache<String, String> thr,
            @Qualifier("resetTicketCache") Cache<String, String> tkt
    ) {
        this.otp = otp;
        this.thr = thr;
        this.tkt = tkt;
    }

    private String ck(String email){ return "code:"+email; }
    private String tk(String email){ return "tkt:"+email; }
    private String hk(String email){ return "thr:"+email; }

    @Override public void saveCode(String email, String code) { otp.put(ck(email), code); }
    @Override public String getCode(String email) { return otp.getIfPresent(ck(email)); }
    @Override public void removeCode(String email) { otp.invalidate(ck(email)); }

    @Override public boolean isThrottled(String email) { return thr.getIfPresent(hk(email)) != null; }
    @Override public void throttle(String email) { thr.put(hk(email), "1"); }
    @Override public void clearThrottle(String email) { thr.invalidate(hk(email)); }

    @Override public void saveTicket(String email, String ticket) { tkt.put(tk(email), ticket); }
    @Override public String getTicket(String email) { return tkt.getIfPresent(tk(email)); }
    @Override public void removeTicket(String email) { tkt.invalidate(tk(email)); }
}
