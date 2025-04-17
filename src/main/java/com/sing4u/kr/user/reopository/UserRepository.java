package com.sing4u.kr.user.reopository;

import com.sing4u.kr.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository {
    @Modifying
    @Query("UPDATE User u SET u.deletedAt = :deletedAt WHERE u.id = :id")
    void softDeleteById(@Param("id") Long id, @Param("deletedAt") LocalDateTime deletedAt);

}

