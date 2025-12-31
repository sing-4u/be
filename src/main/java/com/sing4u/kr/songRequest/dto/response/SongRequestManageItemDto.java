package com.sing4u.kr.songRequest.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SongRequestManageItemDto {
    @Schema(description = "신청곡 ID(대표값)", example = "1001")
    private Long songRequestId;

    @Schema(description = "곡 제목", example = "Blue Valentine")
    private String songTitle;

    @Schema(description = "가수명", example = "NMIXX")
    private String singer;

    @Schema(description = "해당 곡의 총 좋아요 수", example = "13")
    private Long totalLikeCount;

    @Schema(description = "앨범 커버 이미지 url", example = "https://i.scdn.co/image/ab67616d00")
    private String albumImageUrl;

    @Schema(description = "해당 곡의 총 추천(신청) 수", example = "4")
    private Long totalRequestCount;

    // JPQL 'SELECT NEW' 프로젝션을 위해 생성자 필요
    public SongRequestManageItemDto(Long songRequestId, String songTitle, String singer, Long totalLikeCount, String albumImageUrl, Long totalRequestCount) {
        this.songRequestId = songRequestId;
        this.songTitle = songTitle;
        this.singer = singer;
        this.totalLikeCount = totalLikeCount;
        this.albumImageUrl = albumImageUrl;
        this.totalRequestCount = totalRequestCount;
    }
}
