package com.sing4u.kr.home.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class HomeRequest {
    private String keyword;
    private int page;
    private int pageSize = 12;
    @NotNull(message = "seed 값은 필수입니다.")
    private long seed;
}
