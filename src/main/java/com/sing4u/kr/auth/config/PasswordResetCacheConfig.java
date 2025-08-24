package com.sing4u.kr.auth.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class PasswordResetCacheConfig {
    @Bean
    public Cache<String, String> otpCodeCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(3))   // 인증코드 3분
                .maximumSize(5_000)
                .build();
    }

    @Bean
    public Cache<String, String> throttleCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(30)) // 재요청 제한 30초
                .maximumSize(5_000)
                .build();
    }

    @Bean
    public Cache<String, String> resetTicketCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(10)) // 리셋 토큰 10분
                .maximumSize(5_000)
                .build();
    }
}
