package com.sing4u.kr.songRequest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SongRequestCalledResponse {
    private Long songRequestId;
    private String calledYn;
}
