package com.sing4u.kr.songRequest.repository;

import com.sing4u.kr.songRequest.dto.response.SongDetailDto;
import com.sing4u.kr.songRequest.entity.SongRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRequestRepository extends JpaRepository<SongRequest, Long> {
    List<SongRequest> findBySessionIdOrderByRequestedAtAsc(Long sessionId);

    @Query(
            value = """
        SELECT sr FROM SongRequest sr
        JOIN sr.session s
        WHERE s.artist.id = :artistId
          AND sr.fanEmail = :email
        ORDER BY sr.requestedAt DESC
    """,
            countQuery = """
        SELECT COUNT(sr.id) FROM SongRequest sr
        JOIN sr.session s
        WHERE s.artist.id = :artistId
          AND sr.fanEmail = :email
    """
    )
    Page<SongDetailDto> findRecommendHistorySongRequests(
            @Param("artistId") Long artistId,
            @Param("email") String email,
            Pageable pageable
    );

}