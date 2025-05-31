package com.sing4u.kr.session.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.sing4u.kr.customSongRequest.dto.response.SongDetailDto;
import com.sing4u.kr.session.entity.Session;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionSongsDto {
    private Long sessionId;
    private LocalDateTime startedAt;
    private LocalDateTime closedAt;
    private List<SongDetailDto> songs;

    public static SessionSongsDto from(Session session, List<SongDetailDto> songDetails) {
        return SessionSongsDto.builder()
                .sessionId(session.getId())
                .startedAt(session.getStartedAt())
                .closedAt(session.getClosedAt())
                .songs(songDetails) // 파라미터로 받은 리스트 사용
                .build();
    }

}