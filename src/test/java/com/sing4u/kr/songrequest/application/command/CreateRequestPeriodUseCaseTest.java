package com.sing4u.kr.songrequest.application.command;



import com.sing4u.kr.songrequest.application.dto.CreateRequestPeriodCommand;
import com.sing4u.kr.songrequest.application.dto.RequestPeriodResponse;
import com.sing4u.kr.songrequest.domain.RequestPeriod;
import com.sing4u.kr.songrequest.domain.RequestPeriodRepository;
import com.sing4u.kr.songrequest.domain.RequestPeriodStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class CreateRequestPeriodUseCaseTest {

    @Mock
    RequestPeriodRepository repository;

    @InjectMocks
    CreateRequestPeriodUseCase useCase;

    @Test
    void 정상적으로_신청곡_기간을_생성한다() {
        UUID artistId = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.now().plusDays(1);

        CreateRequestPeriodCommand command = new CreateRequestPeriodCommand(artistId, start);
        RequestPeriod period = RequestPeriod.create(artistId, start);

        when(repository.save(any(RequestPeriod.class))).thenReturn(period);

        RequestPeriodResponse response = useCase.execute(command);

        assertThat(response.startAt()).isEqualTo(start);
        assertThat(response.requestPeriodStatus()).isEqualTo(RequestPeriodStatus.OPEN);
        verify(repository).save(any(RequestPeriod.class));
    }
}
