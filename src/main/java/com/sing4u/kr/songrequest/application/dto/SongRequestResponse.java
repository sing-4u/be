package com.sing4u.kr.songrequest.application.dto;

import com.sing4u.kr.songrequest.domain.SongRequest;

import java.time.LocalDateTime;
import java.util.UUID;

public record SongRequestResponse(
    UUID id,
    String songTitle,
    String artistName,
    LocalDateTime requestedAt
) {
    public static SongRequestResponse from(SongRequest request) {
        return new SongRequestResponse(
            request.getId(),
            request.titleOfSong(),
            request.nameOfArtist(),
            request.requestedAt()
        );
    }
}