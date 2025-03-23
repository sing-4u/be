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

    private String nickname;

    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    public static User create() {
        User user = new User();
        return user;
    }

    public void updateProfile(String nickname) {

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
}
