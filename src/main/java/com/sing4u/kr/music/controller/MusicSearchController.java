package com.sing4u.kr.music.controller;

import com.sing4u.kr.common.dto.ResponseResult;
import com.sing4u.kr.common.enums.ResponseCode;
import com.sing4u.kr.common.exception.ApiException;
import com.sing4u.kr.music.MusicInterface;
import com.sing4u.kr.music.MusicPlatformFactory;
import com.sing4u.kr.music.dto.TrackDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/music")
@RequiredArgsConstructor
public class MusicSearchController {
    private final MusicPlatformFactory platformFactory;

    @Operation(summary = "곡 검색", description = "플랫폼 연동을 통해 곡을 검색합니다. (현재는 SPOTIFY만).")
    @GetMapping("/search")
    public ResponseResult<List<TrackDto>> searchTracks(
            @Parameter(description = "검색어 (예: 'Golden')") @RequestParam @NotBlank String q,
            @Parameter(description = "플랫폼 식별자 (기본: SPOTIFY)") @RequestParam(defaultValue = "SPOTIFY") String platform,
            @Parameter(description = "가져올 개수 (1~50)") @RequestParam(defaultValue = "10") @Min(1) @Max(50) int limit,
            @Parameter(description = "페이지 오프셋 (0부터)") @RequestParam(defaultValue = "0") @Min(0) int offset
    ) {
        MusicInterface svc = platformFactory.getService(platform)
                .orElseThrow(() -> new ApiException("INVALID_PLATFORM", "지원하지 않는 플랫폼: " + platform));

        List<TrackDto> items = svc.searchTracks(q, limit, offset, null);
        return new ResponseResult<>(ResponseCode.SUCCESS, items);
    }
}
