package com.sing4u.kr.user.domain;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Access(AccessType.FIELD)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    public void updateProfile(String username, String email) {
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
