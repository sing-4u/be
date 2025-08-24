package com.sing4u.kr.songRequest.entity;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", updatable = false)
    private Session session;

    @Column(name = "fan_email", length = 50)
    private String fanEmail;

    @Column(name = "music_platform_name", length = 20) // 예: "SPOTIFY",
    private String musicPlatformName = "SPOTIFY";

    @Column(name = "platform_track_id", length = 100) // 해당 플랫폼에서의 트랙 ID
    private String platformTrackId;

    @Column(name = "song_title", length = 100)
    private String songTitle;

    @Column(name = "song_artist_name", length = 100)
    private String songArtistName; // 노래의 아티스트명

    @CreationTimestamp
    @Column(name = "requested_at", nullable = false, updatable = false)
    private LocalDateTime requestedAt;

}


