package com.sing4u.kr.user.entity;

import com.sing4u.kr.user.entity.enums.AccountType;
import com.sing4u.kr.user.entity.enums.ActivityPlatformType;
import com.sing4u.kr.user.entity.enums.SocialType;
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
    private AccountType accountType;

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

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public static User of(String nickname, String email, String password, AccountType accountType) {
        return User.builder()
                .nickname(nickname)
                .email(email)
                .password(password)
                .accountType(accountType)
                .build();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void updateIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void updateMainCoverUrl(String mainCoverUrl) {
        this.mainCoverUrl = mainCoverUrl;
    }

    public void updateActivityPlatform(String url, ActivityPlatformType type) {
        this.activityPlatformUrl = url;
        this.activityPlatformType = type;
    }
}
