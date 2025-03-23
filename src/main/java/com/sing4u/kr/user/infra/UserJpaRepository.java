package com.sing4u.kr.user.infra;

import com.sing4u.kr.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<User, UUID> {

}
