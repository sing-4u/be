package com.sing4u.kr.songRequest.like.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SongRequestLikeResponse {
    private final boolean liked;
    private final long likeCount;
}
