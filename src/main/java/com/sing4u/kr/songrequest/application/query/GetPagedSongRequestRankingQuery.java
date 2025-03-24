package com.sing4u.kr.songrequest.application.query;

import com.sing4u.kr.common.response.PagingResponse;
import com.sing4u.kr.songrequest.application.query.dao.SongRequestRankingDao;
import com.sing4u.kr.songrequest.application.query.dto.SongRankingView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GetPagedSongRequestRankingQuery {

    private final SongRequestRankingDao dao;

    public GetPagedSongRequestRankingQuery(SongRequestRankingDao dao) {
        this.dao = dao;
    }

    public PagingResponse<SongRankingView> execute(UUID periodId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SongRankingView> result = dao.findRankedSongRequests(periodId, pageable);
        return PagingResponse.of(result);
    }
}