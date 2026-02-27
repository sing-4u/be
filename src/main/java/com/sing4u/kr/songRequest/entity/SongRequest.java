package com.sing4u.kr.songRequest.entity;

import com.sing4u.kr.session.entity.Session;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class) // 업데이트 처리를 위해 추가
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

    @Builder.Default
    @Column(name = "music_platform_name", length = 20) // 예: "SPOTIFY",
    private String musicPlatformName = "SPOTIFY";

    @Column(name = "platform_track_id", length = 100) // 해당 플랫폼에서의 트랙 ID
    private String platformTrackId;

    @Column(name = "song_title", length = 100)
    private String songTitle;

    @Column(name = "song_artist_name", length = 100)
    private String songArtistName; // 노래의 아티스트명

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "song_request_tags", joinColumns = @JoinColumn(name = "song_request_id"))
    @Column(name = "tag", length = 20)
    private List<String> tags;

    @Column(name = "url", length = 255)
    private String url;

    @CreatedDate
    @Column(name = "requested_at", nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    @Builder.Default
    @Column(nullable=false)
    private long likeCount = 0L;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0)
            this.likeCount--;
    }

    @Column(name = "album_image_url")
    private String albumImageUrl;


    @Column(name = "saved_yn", length = 1, nullable = false)
    @Builder.Default
    private String savedYn = "N";

    @Column(name = "saved_at")
    private LocalDateTime savedAt;

    public void markSaved() {
        this.savedYn = "Y";
        this.savedAt = LocalDateTime.now();
    }

    public void unmarkSaved() {
        this.savedYn = "N";
        this.savedAt = null;
    }

    @Column(name = "called_yn", length = 1, nullable = false)
    @Builder.Default
    private String calledYn = "N";

    public void markCalled() {
        this.calledYn = "Y";
    }
}


