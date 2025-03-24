package com.sing4u.kr.songrequest.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateRequestPeriodCommand(
    UUID artistId,
    LocalDateTime startAt
) {}