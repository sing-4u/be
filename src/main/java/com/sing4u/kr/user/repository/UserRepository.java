package com.sing4u.kr.user.repository;

import com.sing4u.kr.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository {
    Optional<User> findByIdAndDeletedAtIsNull(Long id);
    Optional<User> findByEmailDeletedAtisNull(String email);
}

