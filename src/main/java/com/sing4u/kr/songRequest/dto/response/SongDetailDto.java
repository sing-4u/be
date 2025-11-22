package com.sing4u.kr.songRequest.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sing4u.kr.songRequest.entity.SongRequest;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드 결과 JSON에서 제외
public class SongDetailDto {
    private Long songRequestId;
    private String songTitle;
    private String singer; // 노래의 아티스트 (songArtistName)
    private String email;  // 신청자 이메일 (fanEmail)
    private String spotifyTrackId;
    private LocalDateTime requestedAt;
    private Long likeCount;
    private String albumImageUrl;
    private List<String> tags;
    private String url;


    public static SongDetailDto from(SongRequest songRequest) {
        if (songRequest == null) {
            return null;
        }

        return SongDetailDto.builder()
                .songRequestId(songRequest.getId())
                .songTitle(songRequest.getSongTitle())
                .singer(songRequest.getSongArtistName())
                .email(songRequest.getFanEmail()) // 그룹 기준에서는 신청자 정보는 의미 없음
                .spotifyTrackId(songRequest.getPlatformTrackId())
                .requestedAt(songRequest.getRequestedAt())
                .likeCount(songRequest.getLikeCount())
                .albumImageUrl(songRequest.getAlbumImageUrl())
                .tags(songRequest.getTags())
                .url(songRequest.getUrl())
                .build();
    }
}