package com.sing4u.kr.home.dto.response;

import com.sing4u.kr.common.response.PagingResponse;
import com.sing4u.kr.user.dto.response.UserListResponse;
import lombok.*;

public record HomeResponse(String seed, PagingResponse<UserListResponse> page) {
}
