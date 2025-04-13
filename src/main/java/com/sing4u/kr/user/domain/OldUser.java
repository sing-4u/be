package com.sing4u.kr.user.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
@Access(AccessType.FIELD)
@Table(name = "users")
public class OldUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String profileImageUrl;

    private String nickname;

    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    private SignupType signupType;

    private String refreshToken;

    public OldUser() {}

    public OldUser(UUID id, String email, String nickname, String password, SignupType signupType, UserRole role) {
        if (email == null || nickname == null || signupType == null || role == null) {
            throw new IllegalArgumentException("필수 값 누락");
        }
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.signupType = signupType;
        this.userRole = role;
    }

    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void updateNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("닉네임은 필수입니다.");
        }
        this.nickname = nickname;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void changeRole(UserRole newRole) {
        this.userRole = newRole;
    }

    public void addSongRequestSession(Object session) {
    }

    public void closeSongRequestSession(Object session) {
    }

    public boolean canRequestSong(Object session) {
        return true;
    }

    public boolean isArtist() {
        return this.userRole == UserRole.ARTIST;
    }

    public boolean isUser() {
        return this.userRole == UserRole.USER;
    }

    public boolean isLocalUser() {
        return this.signupType == SignupType.LOCAL;
    }

    public boolean isSocialUser() {
        return this.signupType != SignupType.LOCAL;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void clearRefreshToken() {
        this.refreshToken = null;
    }

    public boolean hasValidRefreshToken(String token) {
        return this.refreshToken != null && this.refreshToken.equals(token);
    }
}
