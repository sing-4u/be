package com.sing4u.kr.songrequest.application.dto;

import com.sing4u.kr.songrequest.domain.RequestPeriod;

import java.time.LocalDateTime;
import java.util.UUID;

public record RequestPeriodResponse(
    UUID id,
    LocalDateTime startAt,
    LocalDateTime endAt
) {
    public static RequestPeriodResponse from(RequestPeriod period) {
        return new RequestPeriodResponse(
            period.getId(),
            period.openedAt(),
            period.closedAt()
        );
    }
}