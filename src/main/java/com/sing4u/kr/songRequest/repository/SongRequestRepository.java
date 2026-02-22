package com.sing4u.kr.songRequest.repository;

import com.sing4u.kr.songRequest.dto.response.SongDetailDto;
import com.sing4u.kr.songRequest.dto.response.SongRequestManageItemDto;
import com.sing4u.kr.songRequest.entity.SongRequest;
import com.sing4u.kr.songRequest.enums.SortType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SongRequestRepository extends JpaRepository<SongRequest, Long> {
    List<SongRequest> findBySessionIdOrderByRequestedAtAsc(Long sessionId);

    @Query(
            value = """
        SELECT DISTINCT sr FROM SongRequest sr
        JOIN sr.session s
        LEFT JOIN sr.tags t
        WHERE s.artist.id = :artistId
          AND sr.fanEmail = :email
          AND (
            :keyword IS NULL OR :keyword = '' OR
            sr.songTitle LIKE CONCAT('%', :keyword, '%') OR
            sr.songArtistName LIKE CONCAT('%', :keyword, '%') OR
            t LIKE CONCAT('%', :keyword, '%')
          )
        ORDER BY sr.requestedAt DESC
    """,
            countQuery = """
        SELECT COUNT(DISTINCT sr.id) FROM SongRequest sr
        JOIN sr.session s
        LEFT JOIN sr.tags t
        WHERE s.artist.id = :artistId
          AND sr.fanEmail = :email
          AND (
            :keyword IS NULL OR :keyword = '' OR
            sr.songTitle LIKE CONCAT('%', :keyword, '%') OR
            sr.songArtistName LIKE CONCAT('%', :keyword, '%') OR
            t LIKE CONCAT('%', :keyword, '%')
          )
    """
    )
    Page<SongDetailDto> findRecommendHistoryLatest(
            @Param("artistId") Long artistId,
            @Param("email") String email,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query(
            value = """
        SELECT DISTINCT sr FROM SongRequest sr
        JOIN sr.session s
        LEFT JOIN sr.tags t
        WHERE s.artist.id = :artistId
          AND sr.fanEmail = :email
          AND (
            :keyword IS NULL OR :keyword = '' OR
            sr.songTitle LIKE CONCAT('%', :keyword, '%') OR
            sr.songArtistName LIKE CONCAT('%', :keyword, '%') OR
            t LIKE CONCAT('%', :keyword, '%')
          )
        ORDER BY sr.likeCount DESC, sr.requestedAt DESC
    """,
            countQuery = """
        SELECT COUNT(DISTINCT sr.id) FROM SongRequest sr
        JOIN sr.session s
        LEFT JOIN sr.tags t
        WHERE s.artist.id = :artistId
          AND sr.fanEmail = :email
          AND (
            :keyword IS NULL OR :keyword = '' OR
            sr.songTitle LIKE CONCAT('%', :keyword, '%') OR
            sr.songArtistName LIKE CONCAT('%', :keyword, '%') OR
            t LIKE CONCAT('%', :keyword, '%')
          )
    """
    )
    Page<SongDetailDto> findRecommendHistoryPopular(
            @Param("artistId") Long artistId,
            @Param("email") String email,
            @Param("keyword") String keyword,
            Pageable pageable
    );


    // TODO: songTitle/songArtistName 표기 차이(직접 입력한 곡과 스포티파이 검색 곡 차이)로 동일 곡이 분리 집계될 수 있을 것 같아서 해결방안 생각해야 함
    @Query(
            value = """
            SELECT new com.sing4u.kr.songRequest.dto.response.SongRequestManageItemDto(
                MIN(sr.id),
                sr.songTitle,
                sr.songArtistName,
                COALESCE(SUM(sr.likeCount), 0),
                MAX(sr.albumImageUrl),
                COUNT(sr.id)
            )
            FROM SongRequest sr
            JOIN sr.session s
            WHERE s.artist.id = :artistId
              AND sr.calledYn = 'N'
              AND (
                    :keyword IS NULL OR :keyword = '' OR
                    LOWER(sr.songTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                    LOWER(sr.songArtistName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  )
            GROUP BY sr.songTitle, sr.songArtistName
            ORDER BY COALESCE(SUM(sr.likeCount), 0) DESC, COUNT(sr.id) DESC
        """,
            countQuery = """
            SELECT COUNT(DISTINCT CONCAT(sr.songTitle, '||', sr.songArtistName))
            FROM SongRequest sr
            JOIN sr.session s
            WHERE s.artist.id = :artistId
              AND sr.calledYn = 'N'
              AND (
                    :keyword IS NULL OR :keyword = '' OR
                    LOWER(sr.songTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                    LOWER(sr.songArtistName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  )
        """
    )
    Page<SongRequestManageItemDto> findArtistSongRequestsForManage(
            @Param("artistId") Long artistId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    Optional<SongRequest> findByIdAndSession_Artist_Id(Long id, Long artistId);

    @Query(
            value = """
            SELECT DISTINCT sr FROM SongRequest sr
            JOIN sr.session s
            LEFT JOIN sr.tags t
            WHERE s.artist.id = :artistId
              AND sr.savedYn = 'Y'
              AND sr.calledYn = 'N'
              AND (
                :keyword IS NULL OR :keyword = '' OR
                LOWER(sr.songTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(sr.songArtistName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(t) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
        """,
            countQuery = """
            SELECT COUNT(DISTINCT sr.id) FROM SongRequest sr
            JOIN sr.session s
            LEFT JOIN sr.tags t
            WHERE s.artist.id = :artistId
              AND sr.savedYn = 'Y'
              AND sr.calledYn = 'N'
              AND (
                :keyword IS NULL OR :keyword = '' OR
                LOWER(sr.songTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(sr.songArtistName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(t) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
        """
    )
    Page<SongRequest> findSavedArtistSongRequests(
            @Param("artistId") Long artistId,
            @Param("keyword") String keyword, // sessionId 파라미터 제거됨
            Pageable pageable
    );
}