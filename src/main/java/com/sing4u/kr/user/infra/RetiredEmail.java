package com.sing4u.kr.user.infra;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "retired_emails")
public class RetiredEmail {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;

    protected RetiredEmail() {}

    public RetiredEmail(String email) {
        this.email = email;
        this.createdAt = LocalDateTime.now();
        this.expiredAt = createdAt.plusDays(30);
    }

    public boolean isExpired(LocalDateTime now) {
        return now.isAfter(expiredAt);
    }

    public String getEmail() {
        return email;
    }
}

