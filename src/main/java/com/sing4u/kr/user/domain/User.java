package com.sing4u.kr.user.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
@Access(AccessType.FIELD)
@Table(name = "users")
public class User {

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

    public static User create() {
        User user = new User();
        return user;
    }

    public static User createGoogleUser() {
        return null;
    }

    public void updateProfileImage(String profileImageUrl) {
    }

    public void updateNickname(String nickname) {
    }

    public void updateEmail(String email) {
    }

    public void changeRole(Object newRole) {
    }

    public void addSongRequestSession(Object session) {
    }

    public void closeSongRequestSession(Object session) {
    }

    public boolean canRequestSong(Object session) {
        return true;
    }

    public boolean isArtist() {
        return true;
    }

    public boolean isUser() {
        return true;
    }

    public boolean isLocalUser() {
        return this.signupType == SignupType.LOCAL;
    }

    public boolean isSocialUser() {
        return this.signupType != SignupType.LOCAL;
    }
}
