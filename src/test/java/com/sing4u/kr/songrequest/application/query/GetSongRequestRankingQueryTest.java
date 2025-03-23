package com.sing4u.kr.songrequest.application.query;

import com.sing4u.kr.songrequest.application.dto.SongRankResponse;
import com.sing4u.kr.songrequest.domain.SongRequest;
import com.sing4u.kr.songrequest.domain.SongRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class GetSongRequestRankingQueryTest {

    @Mock
    SongRequestRepository repository;

    GetSongRequestRankingQuery query;

    @BeforeEach
    void setUp() {
        query = new GetSongRequestRankingQuery(repository);
    }

    @Test
    void 신청곡을_곡명별로_집계하여_랭킹순으로_반환한다() {
        UUID periodId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        List<SongRequest> mockData = List.of(
                SongRequest.create(userId, periodId, "사건의 지평선", "윤하", null),
                SongRequest.create(userId, periodId, "사건의 지평선", "윤하", null),
                SongRequest.create(userId, periodId, "그대라는 시", "태연", null)
        );

        when(repository.findByRequestPeriodId(periodId)).thenReturn(mockData);

        List<SongRankResponse> result = query.execute(periodId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).songTitle()).isEqualTo("사건의 지평선");
        assertThat(result.get(0).count()).isEqualTo(2L);
        verify(repository).findByRequestPeriodId(periodId);
    }
}