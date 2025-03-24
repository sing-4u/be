package com.sing4u.kr.songrequest.application.query.dao;

import com.sing4u.kr.songrequest.application.query.dto.SongRankingView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface SongRequestRankingDao extends Repository<SongRankingView, UUID> {

    @Query("""
                SELECT new com.sing4u.kr.songrequest.application.query.dto.SongRankingView(
                    sr.songTitle, COUNT(sr.id)
                )
                FROM SongRequest sr
                WHERE sr.requestPeriodId = :periodId
                GROUP BY sr.songTitle
                ORDER BY COUNT(sr.id) DESC
            """)
    Page<SongRankingView> findRankedSongRequests(@Param("periodId") UUID periodId, Pageable pageable);
}