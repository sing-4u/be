package com.sing4u.kr.songrequest.application.query;

import com.sing4u.kr.common.response.PagingResponse;
import com.sing4u.kr.songrequest.application.query.dao.SongRequestRankingDao;
import com.sing4u.kr.songrequest.application.query.dto.SongRankingView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class GetPagedSongRequestRankingQueryTest {

    @Mock
    SongRequestRankingDao dao;

    GetPagedSongRequestRankingQuery query;

    @BeforeEach
    void setUp() {
        query = new GetPagedSongRequestRankingQuery(dao);
    }

    @Test
    void 신청곡_랭킹을_페이지로_조회한다() {
        UUID periodId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 5);

        List<SongRankingView> mockResult = List.of(
                new SongRankingView("사건의 지평선", 3L),
                new SongRankingView("그대라는 시", 2L)
        );

        Page<SongRankingView> page = new PageImpl<>(mockResult, pageable, mockResult.size());
        when(dao.findRankedSongRequests(periodId, pageable)).thenReturn(page);

        PagingResponse<SongRankingView> response = query.execute(periodId, 0, 5);

        assertThat(response.getData()).hasSize(2);
        assertThat(response.getData().getFirst().songTitle()).isEqualTo("사건의 지평선");
        assertThat(response.getPage()).isZero();
        assertThat(response.getSize()).isEqualTo(5);
        assertThat(response.isHasNext()).isFalse();

        verify(dao).findRankedSongRequests(periodId, pageable);
    }
}
