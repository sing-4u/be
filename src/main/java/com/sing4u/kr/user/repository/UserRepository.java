package com.sing4u.kr.user.repository;

import com.sing4u.kr.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository {
}

