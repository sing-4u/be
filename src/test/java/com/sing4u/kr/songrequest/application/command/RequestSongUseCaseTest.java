// 📁 src/test/java/com/sing4u/kr/songrequest/application/command/RequestSongUseCaseTest.java
package com.sing4u.kr.songrequest.application.command;

import com.sing4u.kr.common.exception.ApiException;
import com.sing4u.kr.songrequest.application.dto.RequestSongCommand;
import com.sing4u.kr.songrequest.application.dto.SongRequestResponse;
import com.sing4u.kr.songrequest.domain.RequestPeriod;
import com.sing4u.kr.songrequest.domain.RequestPeriodRepository;
import com.sing4u.kr.songrequest.domain.SongRequest;
import com.sing4u.kr.songrequest.domain.SongRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class RequestSongUseCaseTest {

    @Mock
    RequestPeriodRepository periodRepository;

    @Mock
    SongRequestRepository songRequestRepository;

    @InjectMocks
    RequestSongUseCase useCase;

    @Test
    void 정상적으로_신청곡을_요청한다() {
        // given
        UUID requesterId = UUID.randomUUID();
        UUID periodId = UUID.randomUUID();
        String songTitle = "그대라는 시";
        String artistName = "태연";

        RequestSongCommand command = new RequestSongCommand(
                requesterId, periodId, songTitle, artistName
        );

        LocalDateTime now = LocalDateTime.now();
        RequestPeriod period = RequestPeriod.create(UUID.randomUUID(), now.minusDays(1));
        when(periodRepository.findById(periodId)).thenReturn(Optional.of(period));

        SongRequest saved = SongRequest.create(requesterId, periodId, songTitle, artistName);
        when(songRequestRepository.save(any())).thenReturn(saved);

        // when
        SongRequestResponse response = useCase.execute(command);

        // then
        assertThat(response.songTitle()).isEqualTo(songTitle);
        assertThat(response.artistName()).isEqualTo(artistName);
        verify(songRequestRepository).save(any(SongRequest.class));
    }

    @Test
    void 기간이_없으면_예외를_던진다() {
        // given
        UUID periodId = UUID.randomUUID();
        RequestSongCommand command = new RequestSongCommand(
                UUID.randomUUID(), periodId, "노래", "가수"
        );

        when(periodRepository.findById(periodId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("신청곡 기간이 존재하지 않습니다.");
    }

    @Test
    void 신청기간이_아니면_예외를_던진다() {
        // given
        UUID periodId = UUID.randomUUID();
        RequestSongCommand command = new RequestSongCommand(
                UUID.randomUUID(), periodId, "노래", "가수"
        );

        RequestPeriod expired = RequestPeriod.create(UUID.randomUUID(), LocalDateTime.now().minusDays(3));
        expired.close();
        ReflectionTestUtils.setField(expired, "id", periodId);

        when(periodRepository.findById(periodId)).thenReturn(Optional.of(expired));

        // when & then
        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("신청 가능 기간이 아닙니다.");
    }
}