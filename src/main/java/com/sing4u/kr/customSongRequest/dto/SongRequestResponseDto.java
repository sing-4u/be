package com.sing4u.kr.customSongRequest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SongRequestResponseDto {
    private Long songRequestId;
    private String message;
}