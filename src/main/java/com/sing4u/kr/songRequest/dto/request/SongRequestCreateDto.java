package com.sing4u.kr.songRequest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SongRequestCreateDto {
    @NotNull(message = "아티스트 ID는 필수입니다.")
    @Schema(description = "곡을 요청할 아티스트의 공개 ID", example = "user_public_id_1")
    private String artistPublicId;

    @NotNull(message = "세션 ID는 필수입니다.")
    @Schema(description = "요청이 속하게 될 세션의 ID", example = "1")
    private Long sessionId;

    @Size(max = 50, message = "이메일은 최대 50자까지 가능합니다.")
    @Schema(description = "신청자 이메일 (선택 사항)", example = "fan@example.com")
    private String email;

    @Size(max = 20, message = "음악 플랫폼 이름은 최대 20자까지 가능합니다.")
    @Schema(hidden = true) // Swagger 문서에서 숨김
    private final String musicPlatformName = "SPOTIFY";

    @Size(max = 100, message = "플랫폼 트랙 ID는 최대 100자까지 가능합니다.")
    @Schema(description = "음원 플랫폼의 트랙 ID (정보 조회에 사용)", example = "4iV5W9uYEdYUVa79Axb7Rh")
    private String platformTrackId;

    @Size(max = 100, message = "곡 제목은 최대 100자까지 가능합니다.")
    @Schema(description = "직접 입력할 곡 제목 (플랫폼 ID 없을 시 사용)", example = "Hype Boy")
    private String songTitle;

    @Size(max = 100, message = "가수명은 최대 100자까지 가능합니다.")
    @Schema(description = "직접 입력할 가수명 (플랫폼 ID 없을 시 사용)", example = "NewJeans")
    private String artistName;}