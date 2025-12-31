package com.sing4u.kr.session.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sing4u.kr.session.entity.Session;
import com.sing4u.kr.session.enums.SessionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArtistSessionResponseDto {
    @Schema(description = "세션 ID", example = "1")
    private Long sessionId;

    @Schema(description = "세션 상태", example = "OPEN",implementation = SessionStatus.class)
    private SessionStatus status;

    @Schema(description = "세션 시작 시각", example = "2025-06-16T10:00:00")
    private LocalDateTime startedAt;

    @Schema(description = "세션 종료 시각 (종료되지 않았으면 null)", example = "2025-06-16T12:00:00")
    private LocalDateTime closedAt;

    @Schema(description = "세션 당 추천곡 수 합계", example = "128")
    private Long songRequestCount;

    //JPQL용
    public ArtistSessionResponseDto(Long sessionId, SessionStatus status, LocalDateTime startedAt, LocalDateTime closedAt, Long songRequestCount) {
        this.sessionId = sessionId;
        this.status = status;
        this.startedAt = startedAt;
        this.closedAt = closedAt;
        this.songRequestCount = songRequestCount;
    }
}
