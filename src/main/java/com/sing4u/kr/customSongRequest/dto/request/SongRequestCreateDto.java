package com.sing4u.kr.customSongRequest.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class SongRequestCreateDto {
    @NotNull(message = "아티스트 ID는 필수입니다.")
    private Long artistId;

    @NotNull(message = "세션 ID는 필수입니다.")
    private Long sessionId;

    @Size(max = 50, message = "이메일은 최대 50자까지 가능합니다.")
    private String email;

    @Size(max = 20, message = "음악 플랫폼 이름은 최대 20자까지 가능합니다.")
    private String musicPlatformName; // 예: "SPOTIFY", "YOUTUBE_MUSIC"

    @Size(max = 100, message = "플랫폼 트랙 ID는 최대 100자까지 가능합니다.")
    private String platformTrackId;

    @Size(max = 100, message = "곡 제목은 최대 100자까지 가능합니다.")
    private String songTitle;

    @Size(max = 100, message = "가수명은 최대 100자까지 가능합니다.")
    private String artistName;
}