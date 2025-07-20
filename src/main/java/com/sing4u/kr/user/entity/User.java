package com.sing4u.kr.user.entity;

import com.sing4u.kr.user.entity.enums.UserType;
import com.sing4u.kr.user.entity.enums.SocialType;
import com.sing4u.kr.user.enums.UserRole;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String userPublicId;

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

    @Column(name = "is_open", nullable = false)
    private boolean isOpen;

    public static User of(String nickname, String email, String password, UserType userType) {

        UserRole role;
        if (UserType.ARTIST.equals(userType)) {
            role = UserRole.ARTIST;
        } else if(UserType.FAN.equals(userType)) {
            role = UserRole.USER;
        } else {
            role = UserRole.ADMIN;
        }

        return User.builder()
                .nickname(nickname)
                .email(email)
                .password(password)
                .userType(userType)
                .role(role)
                .isOpen(false)
                .build();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public void updateAccountType(UserType userType) {
        this.userType = userType;
    }

    public void updateProfile(String nickname, String introduction, String mainCoverUrl) {
        this.nickname = nickname;
        this.introduction = introduction;
        this.mainCoverUrl = mainCoverUrl;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void updateIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }
    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public static User testUserBuilder(Long id, String userPublicId, String nickName, UserType userType) {
        return User.builder()
                .id(id)
                .userPublicId(userPublicId)
                .nickname(nickName)
                .userType(userType)
                .build();

    }
    public static User ofOAuth2(String email, String name, String encodedPassword, SocialType socialType) {
        return User.builder()
                .email(email)
                .nickname(name)
                .password(encodedPassword)
                .role(UserRole.USER)
                .userType(UserType.FAN)
                .socialType(socialType)
                .build();
    }

    @PrePersist
    public void generatePublicId() {
        if (this.userPublicId == null) {
            this.userPublicId = UUID.randomUUID().toString();
        }
    }
}
