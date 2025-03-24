package com.sing4u.kr.songrequest.application.query.dao;

import com.sing4u.kr.songrequest.application.query.dto.RequestPeriodView;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RequestPeriodDao extends Repository<RequestPeriodView, UUID> {

    @Query("""
        SELECT new com.sing4u.kr.songrequest.application.query.dto.RequestPeriodView(
            r.id, r.startAt, r.endAt,
            CASE WHEN r.requestPeriodStatus = 'OPEN' AND r.startAt <= CURRENT_TIMESTAMP AND (r.endAt IS NULL OR r.endAt > CURRENT_TIMESTAMP)
                 THEN true ELSE false END
        )
        FROM RequestPeriod r
        WHERE r.artistId = :artistId
        ORDER BY r.startAt DESC
    """)
    List<RequestPeriodView> findRequestPeriodViewsByArtistId(@Param("artistId") UUID artistId);
}
