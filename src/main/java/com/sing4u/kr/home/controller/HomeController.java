package com.sing4u.kr.home.controller;

import com.sing4u.kr.common.dto.ResponseResult;
import com.sing4u.kr.common.enums.ResponseCode;
import com.sing4u.kr.home.dto.request.HomeRequest;
import com.sing4u.kr.user.dto.response.UserListResponse;
import com.sing4u.kr.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;

    @GetMapping("/")
    public ResponseResult<List<UserListResponse>> getArtists(@RequestBody @Valid HomeRequest request) {
        return new ResponseResult<>(ResponseCode.SUCCESS, userService.getArtistList(request));
    }
}
