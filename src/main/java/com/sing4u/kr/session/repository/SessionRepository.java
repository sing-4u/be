package com.sing4u.kr.session.repository;

import com.sing4u.kr.session.dto.response.ArtistSessionResponseDto;
import com.sing4u.kr.session.entity.Session;
import com.sing4u.kr.session.enums.SessionStatus;
import com.sing4u.kr.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByArtistAndStatus(User artist, SessionStatus status);

    Optional<Session> findByIdAndArtistId(Long sessionId, Long artistId);

    @Query("SELECT DISTINCT s FROM Session s " +
            "LEFT JOIN FETCH s.songRequests sr " +
            "WHERE s.artist.id = :artistId " +
            "ORDER BY s.startedAt DESC")
    List<Session> findAllWithSongRequestsByArtist(@Param("artistId") Long artistId);

    Optional<Session> findByArtistIdAndStatus(Long artistId, SessionStatus status);

    Optional<Session> findTopByArtistIdOrderByStartedAtDesc(Long artistId);

    Page<Session> findByArtistIdOrderByStartedAtDesc(Long artistId, Pageable pageable);
    
    Iterable<Session> findAllByArtistIdAndStatus(Long artistId, SessionStatus sessionStatus);

    @Query("""
        SELECT NEW com.sing4u.kr.session.dto.response.ArtistSessionResponseDto(
            s.id,
            s.status,
            s.startedAt,
            s.closedAt,
            count(sr.id)
        )
        FROM Session s
        LEFT JOIN SongRequest sr ON sr.session = s
        WHERE s.artist.id = :artistId
        GROUP BY s.id, s.status, s.startedAt, s.closedAt
        ORDER BY s.startedAt desc
    """)
    Page<ArtistSessionResponseDto> findArtistSessionsForManage(@Param("artistId") Long artistId, Pageable pageable);
}
