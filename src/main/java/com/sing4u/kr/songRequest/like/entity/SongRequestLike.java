package com.sing4u.kr.songRequest.like.entity;

import com.sing4u.kr.songRequest.entity.SongRequest;
import com.sing4u.kr.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "song_request_like",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_song_request_like_user_req",
                columnNames = {"user_id","song_request_id"}
        )
)
public class SongRequestLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    @JoinColumn(name="song_request_id", nullable=false)
    private SongRequest songRequest;

    @Column(nullable=false, updatable=false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() { this.createdAt = LocalDateTime.now(); }
}
