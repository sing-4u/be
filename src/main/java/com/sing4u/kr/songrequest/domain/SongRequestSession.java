package com.sing4u.kr.songrequest.domain;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Access(AccessType.FIELD)
@Table(name = "song_request_session")
public class SongRequestSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    public boolean isActive() {
        return true;
    }

    public void addRequestedSong(RequestedSong song) {
    }
}
