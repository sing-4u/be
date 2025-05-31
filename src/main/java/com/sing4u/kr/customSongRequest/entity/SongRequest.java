package com.sing4u.kr.customSongRequest.entity;

import com.sing4u.kr.customSongRequest.dto.request.SongRequestCreateDto;
import com.sing4u.kr.session.entity.Session;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter // Builder 사용 시 일부 필드 수정을 위해
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SongRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // songRequestId

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "fan_email", length = 50)
    private String fanEmail;

    // PR #1 에서는 이 필드를 사용한 Spotify API 연동은 하지 않음. 값 저장만 가능.
    @Column(name = "spotify_track_id", length = 100)
    private String spotifyTrackId;

    @Column(name = "song_title", length = 100)
    private String songTitle;

    @Column(name = "song_artist_name", length = 100)
    private String songArtistName; // 노래의 아티스트명

    @CreationTimestamp
    @Column(name = "requested_at", nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    public static SongRequest fromCreateDto(Session session, SongRequestCreateDto dto) {
        return SongRequest.builder()
                .sessionId(dto.getSessionId())
                .fanEmail(dto.getEmail())
                .songTitle(dto.getSongTitle())
                .songArtistName(dto.getArtistName())
                .spotifyTrackId(dto.getSpotifyTrackId())
                .build();
    }
}


