package com.sing4u.kr.songrequest.application.command;

import com.sing4u.kr.songrequest.application.dto.CreateRequestPeriodCommand;
import com.sing4u.kr.songrequest.application.dto.RequestPeriodResponse;
import com.sing4u.kr.songrequest.domain.RequestPeriod;
import com.sing4u.kr.songrequest.domain.RequestPeriodRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateRequestPeriodUseCase {

    private final RequestPeriodRepository repository;

    @Transactional
    public RequestPeriodResponse execute(CreateRequestPeriodCommand command) {
        RequestPeriod created = RequestPeriod.create(
            command.artistId(),
            command.startAt()
        );
        return RequestPeriodResponse.from(repository.save(created));
    }
}