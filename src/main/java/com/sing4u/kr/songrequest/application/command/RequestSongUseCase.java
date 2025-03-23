package com.sing4u.kr.songrequest.application.command;

import com.sing4u.kr.common.exception.ApiException;
import com.sing4u.kr.common.exception.ExceptionCode;
import com.sing4u.kr.songrequest.application.dto.RequestSongCommand;
import com.sing4u.kr.songrequest.application.dto.SongRequestResponse;
import com.sing4u.kr.songrequest.domain.RequestPeriod;
import com.sing4u.kr.songrequest.domain.RequestPeriodRepository;
import com.sing4u.kr.songrequest.domain.SongRequest;
import com.sing4u.kr.songrequest.domain.SongRequestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RequestSongUseCase {

    private final RequestPeriodRepository periodRepository;
    private final SongRequestRepository requestRepository;


    @Transactional
    public SongRequestResponse execute(RequestSongCommand command) {
        RequestPeriod period = periodRepository.findById(command.requestPeriodId())
                .orElseThrow(() -> new ApiException(ExceptionCode.NOT_FOUND, "신청곡 기간이 존재하지 않습니다."));

        if (!period.isCurrent(LocalDateTime.now())) {
            throw new ApiException(ExceptionCode.BAD_REQUEST, "신청 가능 기간이 아닙니다.");
        }

        SongRequest request = SongRequest.create(
                command.requesterId(),
                command.requestPeriodId(),
                command.songTitle(),
                command.artistName(),
                command.message()
        );

        return SongRequestResponse.from(requestRepository.save(request));
    }
}