package com.sing4u.kr.common.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
@Builder
public class PagingResponse<T> {
    private final List<T> data;
    private final int page;
    private final int size;
    private final Long totalElements; // Page일 경우에만 값 제공
    private final Integer totalPages; // Page일 경우에만 값 제공
    private final boolean hasNext;

    public static <T> PagingResponse<T> of(Page<T> pageData) {
        return PagingResponse.<T>builder()
                .data(pageData.getContent())
                .page(pageData.getNumber())
                .size(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .hasNext(pageData.hasNext())
                .build();
    }

    public static <T> PagingResponse<T> of(Slice<T> sliceData) {
        return PagingResponse.<T>builder()
                .data(sliceData.getContent())
                .page(sliceData.getNumber())
                .size(sliceData.getSize())
                .totalElements(null)
                .totalPages(null)
                .hasNext(sliceData.hasNext())
                .build();
    }

    public static <T> PagingResponse<T> of(List<T> data, int page, int size, long totalElements, boolean hasNext) {
        return PagingResponse.<T>builder()
                .data(data)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages((int) Math.ceil((double) totalElements / size))
                .hasNext(hasNext)
                .build();
    }
}