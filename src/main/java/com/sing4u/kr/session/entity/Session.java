package com.sing4u.kr.session.entity;


import com.sing4u.kr.customSongRequest.entity.SongRequest;
import com.sing4u.kr.session.enums.SessionStatus;
import com.sing4u.kr.user.entity.User;
import jakarta.persistence.*;
import jdk.jfr.Enabled;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 아티스트가 팬들의 신청곡을 받는 세션
@Enabled
@Entity
@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "musicSession")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; // sessionId

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private User artist;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SessionStatus status;

    @CreationTimestamp
    @Column(name = "started_at", nullable =false, updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @OneToMany(
            mappedBy = "session", // SongRequest 엔티티의 session 필드가 외래키 주인
            cascade = CascadeType.ALL, // Session 저장/삭제 시 관련 SongRequest도 함께 처리
            orphanRemoval = true, // 리스트에서 제거된 SongRequest는 DB에서도 자동 삭제
            fetch = FetchType.LAZY // SongRequest는 실제 접근 시점에 DB에서 로딩 (지연 로딩)
    )
    @Builder.Default
    private List<SongRequest> songRequests = new ArrayList<>();

    public static Session create(User artist){
        return Session.builder()
                .artist(artist)
                .status(SessionStatus.OPEN)
                .build();
    }

    public void close(){
        if(this.status == SessionStatus.CLOSE){
            throw new IllegalStateException("Session is already closed");
        }
        this.status = SessionStatus.CLOSE;
        this.closedAt = LocalDateTime.now();
    }
}
