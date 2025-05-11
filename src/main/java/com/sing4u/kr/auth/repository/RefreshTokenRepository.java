package com.sing4u.kr.auth.repository;

import com.sing4u.kr.auth.Entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
