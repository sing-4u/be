package com.sing4u.kr.session.dto.response;

import com.sing4u.kr.session.entity.Session;
import com.sing4u.kr.session.enums.SessionStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CurrentSessionResponseDto {
    private Long sessionId;
    private Long artistId;
    private SessionStatus status;
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