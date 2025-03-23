package com.sing4u.kr.songrequest.application.command;

import com.sing4u.kr.songrequest.application.dto.CreateRequestPeriodCommand;
import com.sing4u.kr.songrequest.application.dto.RequestPeriodResponse;
import com.sing4u.kr.songrequest.domain.RequestPeriod;
import com.sing4u.kr.songrequest.domain.RequestPeriodRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
public class CreateRequestPeriodUseCase {

    private final RequestPeriodRepository repository;

    public CreateRequestPeriodUseCase(RequestPeriodRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public RequestPeriodResponse execute(CreateRequestPeriodCommand command) {
        RequestPeriod created = RequestPeriod.create(
            command.artistId(),
            command.startAt(),
            command.endAt()
        );
        return RequestPeriodResponse.from(repository.save(created));
    }
}