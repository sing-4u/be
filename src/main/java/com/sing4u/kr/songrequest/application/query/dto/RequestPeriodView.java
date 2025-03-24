package com.sing4u.kr.songrequest.application.query.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RequestPeriodView(
        UUID id,
        LocalDateTime startAt,
        LocalDateTime endAt,
        boolean isOpen
) {}