package com.sing4u.kr.user.controller;

import com.sing4u.kr.application.utils.SecurityContextUtils;
import com.sing4u.kr.common.dto.ResponseResult;
import com.sing4u.kr.common.enums.ResponseCode;
import com.sing4u.kr.user.dto.request.UserUpdateActivityPlatformRequest;
import com.sing4u.kr.user.dto.response.UserActivityPlatformResponse;
import com.sing4u.kr.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/platform")
@RequiredArgsConstructor
public class UserPlatformController {
    private final UserService userService;

    @Operation(summary = "내 활동 플랫폼 조회", description = "로그인된 사용자의 활동 플랫폼(유튜브, 인스타그램 등) 목록을 조회합니다.")
    @GetMapping("/me/activity-platform")
    public ResponseResult<UserActivityPlatformResponse> getActivityPlatform() {
        Long userId = SecurityContextUtils.getAccountId();
        return new ResponseResult<>(ResponseCode.SUCCESS, userService.getActivityPlatform(userId));
    }

    @Operation(summary = "내 활동 플랫폼 수정", description = "로그인된 사용자의 활동 플랫폼 목록을 수정/업데이트합니다.")
    @PutMapping("/me/activity-platform")
    public ResponseResult<UserActivityPlatformResponse> updateActivityPlatform(@RequestBody @Valid UserUpdateActivityPlatformRequest request) {
        Long userId = SecurityContextUtils.getAccountId();
        return new ResponseResult<>(ResponseCode.SUCCESS, userService.updateActivityPlatform(userId, request));
    }

    @Operation(summary = "userPublicId로 특정 사용자 활동 플랫폼 조회", description = "userPublicId 특정 사용자의 활동 플랫폼 조회")
    @GetMapping("/{userPublicId}/activity-platform")
    public ResponseResult<UserActivityPlatformResponse> getActivityPlatformByPublicId(@PathVariable("userPublicId") String publicId) {
        Long userId = userService.getUserIdByPublicId(publicId);
        return new ResponseResult<>(ResponseCode.SUCCESS, userService.getActivityPlatform(userId));
    }
}
