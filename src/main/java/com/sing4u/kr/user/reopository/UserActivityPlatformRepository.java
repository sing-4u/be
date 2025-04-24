package com.sing4u.kr.user.reopository;

import com.sing4u.kr.user.entity.UserActivityPlatform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserActivityPlatformRepository extends JpaRepository<UserActivityPlatform, Long> {
    void deleteByUserId(Long userId);
    List<UserActivityPlatform> findAllByUserId(Long userId);
}
