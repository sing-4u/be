package com.sing4u.kr.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 1000)
    private String token;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    public static RefreshToken of(Long userId, String token) {
        return RefreshToken.builder()
                .userId(userId)
                .token(token)
                .build();
    }

    public void update(String newToken) {
        this.token = newToken;
    }

}
