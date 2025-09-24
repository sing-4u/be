package com.sing4u.kr.home.controller;

import com.sing4u.kr.common.dto.ResponseResult;
import com.sing4u.kr.common.enums.ResponseCode;
import com.sing4u.kr.common.response.PagingResponse;
import com.sing4u.kr.home.dto.request.HomeRequest;
import com.sing4u.kr.home.dto.response.HomeResponse;
import com.sing4u.kr.user.dto.response.UserListResponse;
import com.sing4u.kr.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;

    @GetMapping
    public ResponseResult<HomeResponse> getArtists(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required=false) String seed
    ) {
        if (seed == null || seed.isBlank()) seed = UUID.randomUUID().toString();

        HomeRequest request = HomeRequest.builder()
                .page(page)
                .pageSize(pageSize)
                .keyword(keyword)
                .seed(seed)
                .build();

        PagingResponse<UserListResponse> paging = userService.getArtistList(request);
        HomeResponse response = new HomeResponse(seed, paging);

        return new ResponseResult<>(ResponseCode.SUCCESS, response);
    }
}

