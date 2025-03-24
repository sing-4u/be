package com.sing4u.kr.songrequest.application.command;

import com.sing4u.kr.common.exception.ApiException;
import com.sing4u.kr.common.exception.ExceptionCode;
import com.sing4u.kr.songrequest.application.dto.RequestPeriodResponse;
import com.sing4u.kr.songrequest.domain.RequestPeriod;
import com.sing4u.kr.songrequest.domain.RequestPeriodRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CloseRequestPeriodUseCase {

    private final RequestPeriodRepository repository;


    @Transactional
    public RequestPeriodResponse execute(UUID requestPeriodId) {
        RequestPeriod period = repository.findById(requestPeriodId)
                .orElseThrow(() -> new ApiException(ExceptionCode.NOT_FOUND, "신청곡 기간이 존재하지 않습니다."));

        period.close();
        return RequestPeriodResponse.from(period);
    }
}
