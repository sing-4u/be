package com.sing4u.kr.common.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
@Builder
public class PagingResponse<T> {
    private final List<T> content;
    private final int currentPageIndex;
    private final int pageSize;
    private final Long totalElements; // Page일 경우에만 값 제공
    private final Integer totalPages; // Page일 경우에만 값 제공
    private final boolean hasNext;

    public static <T> PagingResponse<T> of(Page<T> pageData) {
        return PagingResponse.<T>builder()
                .content(pageData.getContent())
                .currentPageIndex(pageData.getNumber())
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .hasNext(pageData.hasNext())
                .build();
    }

    public static <T> PagingResponse<T> of(Slice<T> sliceData) {
        return PagingResponse.<T>builder()
                .content(sliceData.getContent())
                .currentPageIndex(sliceData.getNumber())
                .pageSize(sliceData.getSize())
                .totalElements(null)
                .totalPages(null)
                .hasNext(sliceData.hasNext())
                .build();
    }

    public static <T> PagingResponse<T> of(List<T> data, int page, int size, long totalElements, boolean hasNext) {
        return PagingResponse.<T>builder()
                .content(data)
                .currentPageIndex(page)
                .pageSize(size)
                .totalElements(totalElements)
                .totalPages((int) Math.ceil((double) totalElements / size))
                .hasNext(hasNext)
                .build();
    }
}