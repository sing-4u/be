package com.sing4u.kr.music.dto;

import lombok.*;

@Getter
@Builder
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TrackDto {
    private String platformTrackId;
    private String title;
    private String artistName;
    private String platformName;
    private String albumImageUrl;
}
