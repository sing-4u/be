package com.sing4u.kr.user.repository;

import com.sing4u.kr.user.entity.UserActivityPlatform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserActivityPlatformRepository extends JpaRepository<UserActivityPlatform, Long> {
    List<UserActivityPlatform> findAllByUserId(Long userId);
}
