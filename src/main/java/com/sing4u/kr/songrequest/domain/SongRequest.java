package com.sing4u.kr.songrequest.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "song_requests")
public class SongRequest {

    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID requestPeriodId;

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID requesterId;

    @Column(nullable = false)
    private String songTitle;

    @Column(nullable = false)
    private String artistName;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected SongRequest() {}

    public static SongRequest create(UUID requesterId, UUID requestPeriodId, String songTitle, String artistName, String message) {
        SongRequest sr = new SongRequest();
        sr.requesterId = requesterId;
        sr.requestPeriodId = requestPeriodId;
        sr.songTitle = songTitle;
        sr.artistName = artistName;
        sr.createdAt = LocalDateTime.now();
        return sr;

    }

    public UUID getId() {
        return this.id;
    }

    public UUID requestedBy() {
        return this.requesterId;
    }

    public UUID periodIdentifier() {
        return this.requestPeriodId;
    }

    public String titleOfSong() {
        return this.songTitle;
    }

    public String nameOfArtist() {
        return this.artistName;
    }

    public LocalDateTime requestedAt() {
        return this.createdAt;
    }

    public boolean isRequestedBy(UUID userId) {
        return this.requesterId.equals(userId);
    }

    public boolean isRequestedForPeriod(UUID periodId) {
        return this.requestPeriodId.equals(periodId);
    }

}