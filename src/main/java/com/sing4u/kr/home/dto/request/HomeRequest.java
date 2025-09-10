package com.sing4u.kr.home.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class HomeRequest {
    private String keyword;
    private int page;

    @Builder.Default
    private int pageSize = 12;
}
