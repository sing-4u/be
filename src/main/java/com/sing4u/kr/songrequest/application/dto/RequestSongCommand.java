package com.sing4u.kr.songrequest.application.dto;

import java.util.UUID;

public record RequestSongCommand(
    UUID requesterId,
    UUID requestPeriodId,
    String songTitle,
    String artistName,
    String message
) {}
