package com.sing4u.kr.auth.repository;

import com.sing4u.kr.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // 토큰 값으로 RefreshToken을 찾는 메서드
    Optional<RefreshToken> findByToken(String token);

    // 토큰 값으로 RefreshToken을 삭제하는 메서드
    void deleteByToken(String token);

}
