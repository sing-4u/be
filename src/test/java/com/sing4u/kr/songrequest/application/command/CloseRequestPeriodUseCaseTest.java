package com.sing4u.kr.songrequest.application.command;

import com.sing4u.kr.common.exception.ApiException;
import com.sing4u.kr.songrequest.application.dto.RequestPeriodResponse;
import com.sing4u.kr.songrequest.domain.RequestPeriod;
import com.sing4u.kr.songrequest.domain.RequestPeriodRepository;
import com.sing4u.kr.songrequest.domain.RequestPeriodStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class CloseRequestPeriodUseCaseTest {

    @Mock
    RequestPeriodRepository repository;

    CloseRequestPeriodUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CloseRequestPeriodUseCase(repository);
    }

    @Test
    void 신청곡_기간을_정상적으로_종료한다() {
        UUID periodId = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.now().minusDays(1);

        RequestPeriod period = RequestPeriod.create(UUID.randomUUID(), start);
        when(repository.findById(periodId)).thenReturn(Optional.of(period));

        RequestPeriodResponse response = useCase.execute(periodId);

        assertThat(response.requestPeriodStatus()).isEqualTo(RequestPeriodStatus.CLOSED);
        assertThat(response.endAt()).isCloseTo(LocalDateTime.now(), within(1, SECONDS));
    }

    @Test
    void 이미_종료된_기간은_종료할_수_없다() {
        UUID periodId = UUID.randomUUID();
        RequestPeriod period = RequestPeriod.create(UUID.randomUUID(), LocalDateTime.now().minusDays(3));
        period.close();

        when(repository.findById(periodId)).thenReturn(Optional.of(period));

        assertThatThrownBy(() ->
                useCase.execute(periodId)
        ).isInstanceOf(ApiException.class)
                .hasMessageContaining("이미 종료되었습니다.");
    }
}