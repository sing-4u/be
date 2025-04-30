package com.sing4u.kr.user.entity;

import com.sing4u.kr.user.entity.enums.UserType;
import com.sing4u.kr.user.entity.enums.ActivityPlatformType;
import com.sing4u.kr.user.entity.enums.SocialType;
import com.sing4u.kr.user.enums.UserRole;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String nickname;

    @Column(length = 50, nullable = false, unique = true)
    private String email;

    @Column(length = 100, nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type", length = 20)
    private SocialType socialType;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", length = 20, nullable = false)
    private UserType userType;

    @Column(name = "profile_image", length = 255)
    private String profileImage;

    @Column(length = 80)
    private String introduction;

    @Column(name = "main_cover_url", length = 255)
    private String mainCoverUrl;

    @Column(name = "activity_platform_url", length = 255)
    private String activityPlatformUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_platform_type", length = 20)
    private ActivityPlatformType activityPlatformType;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public static User of(String nickname, String email, String password, UserType userType) {
        return User.builder()
                .nickname(nickname)
                .email(email)
                .password(password)
                .userType(userType)
                .build();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public void updateAccountType(UserType userType) {
        this.userType = userType;
    }

    public void updateProfile(String profileImage, String introduction, String mainCoverUrl,
                              String activityPlatformUrl, ActivityPlatformType activityPlatformType) {
        this.profileImage = profileImage;
        this.introduction = introduction;
        this.mainCoverUrl = mainCoverUrl;
        this.activityPlatformUrl = activityPlatformUrl;
        this.activityPlatformType = activityPlatformType;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateDeletedAt() {
        this.deletedAt = LocalDateTime.now();
    }
}
