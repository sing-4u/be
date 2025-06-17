package com.sing4u.kr.session.dto.response;

import com.sing4u.kr.session.entity.Session;
import com.sing4u.kr.session.enums.SessionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CurrentSessionResponseDto {
    @Schema(description = "세션 ID", example = "101")
    private Long sessionId;

    @Schema(description = "세션을 진행 중인 아티스트 ID", example = "1")
    private Long artistId;

    @Schema(description = "세션 상태 (항상 OPEN)", example = "OPEN")
    private SessionStatus status;

    @Schema(description = "세션 시작 시각", example = "2025-06-16T10:00:00")
    private LocalDateTime startedAt;
    public static CurrentSessionResponseDto from(Session session) {
        return CurrentSessionResponseDto.builder()
                .sessionId(session.getId())
                .artistId(session.getArtist().getId())
                .status(session.getStatus())
                .startedAt(session.getStartedAt())
                .build();
    }
}