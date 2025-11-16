package com.sing4u.kr.songRequest.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ArtistSongRequestsResponse {
    private List<SongDetailDto> songs;
    private boolean hasNext;
    private int page;
    private int pageSize;
}
