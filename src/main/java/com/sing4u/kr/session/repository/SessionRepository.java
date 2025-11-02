package com.sing4u.kr.session.repository;

import com.sing4u.kr.session.entity.Session;
import com.sing4u.kr.session.enums.SessionStatus;
import com.sing4u.kr.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.nio.channels.FileChannel;
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

    Iterable<Session> findAllByArtistIdAndStatus(Long artistId, SessionStatus sessionStatus);
}
