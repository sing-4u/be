package com.sing4u.kr.session.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sing4u.kr.session.entity.Session;
import com.sing4u.kr.session.enums.SessionStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionResponseDto {
    private Long sessionId;
    private Long artistId;
    private SessionStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime closedAt;

    public static SessionResponseDto from(Session session) {
        return SessionResponseDto.builder()
                .sessionId(session.getId())
                .artistId(session.getArtist().getId())
                .status(session.getStatus())
                .startedAt(session.getStartedAt())
                .closedAt(session.getClosedAt())
                .build();
    }
}
