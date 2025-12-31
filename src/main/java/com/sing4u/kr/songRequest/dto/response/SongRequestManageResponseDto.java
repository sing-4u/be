package com.sing4u.kr.songRequest.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SongRequestManageResponseDto {
    @Schema(description = "신청곡 리스트")
    private List<SongRequestManageItemDto> songs;

    @Schema(description = "다음 페이지 존재 여부", example = "true")
    private boolean hasNext;

    @Schema(description = "현재 페이지 인덱스(0-base)", example = "0")
    private int page;

    @Schema(description = "페이지 크기", example = "10")
    private int pageSize;

    @Schema(description = "전체 데이터(신청곡 수)", example = "37")
    private long totalElements;

    public static SongRequestManageResponseDto of(Page<SongRequestManageItemDto> pageResult) {
        return SongRequestManageResponseDto.builder()
                .songs(pageResult.getContent())
                .hasNext(pageResult.hasNext())
                .page(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .build();
    }
}
