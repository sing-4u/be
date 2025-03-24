package com.sing4u.kr.songrequest.application.query;

import com.sing4u.kr.songrequest.application.query.dao.RequestPeriodDao;
import com.sing4u.kr.songrequest.application.query.dto.RequestPeriodView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class GetRequestPeriodsByArtistQueryTest {

    @Mock
    RequestPeriodDao dao;

    GetRequestPeriodsByArtistQuery query;

    @BeforeEach
    void setUp() {
        query = new GetRequestPeriodsByArtistQuery(dao);
    }

    @Test
    void 아티스트의_신청기간_목록을_조회한다() {
        UUID artistId = UUID.randomUUID();

        List<RequestPeriodView> mockResult = List.of(
                new RequestPeriodView(UUID.randomUUID(), LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), true),
                new RequestPeriodView(UUID.randomUUID(), LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(2), false)
        );

        when(dao.findRequestPeriodViewsByArtistId(artistId)).thenReturn(mockResult);

        List<RequestPeriodView> result = query.execute(artistId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).isOpen()).isTrue();
        assertThat(result.get(1).isOpen()).isFalse();

        verify(dao).findRequestPeriodViewsByArtistId(artistId);
    }
}
