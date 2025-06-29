package com.sing4u.kr.session.entity;


import com.sing4u.kr.songRequest.entity.SongRequest;
import com.sing4u.kr.session.enums.SessionStatus;
import com.sing4u.kr.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

// 아티스트가 팬들의 신청곡을 받는 세션
@Entity
@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"artist"})
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

    @OneToMany(mappedBy = "session", fetch = FetchType.LAZY)
    private List<SongRequest> songRequests;

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
