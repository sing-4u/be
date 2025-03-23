package com.sing4u.kr.user.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RetiredEmailJpaRepository extends JpaRepository<RetiredEmail, UUID> {
}
