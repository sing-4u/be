package com.sing4u.kr.user.controller;

import com.sing4u.kr.application.utils.SecurityContextUtils;
import com.sing4u.kr.common.dto.ResponseResult;
import com.sing4u.kr.common.enums.ResponseCode;
import com.sing4u.kr.user.dto.request.*;
import com.sing4u.kr.user.dto.response.*;
import com.sing4u.kr.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseResult<UserCreateResponse> createUser(@RequestBody @Valid UserCreateRequest request) {
        return new ResponseResult<>(ResponseCode.SUCCESS, userService.createUser(request));
    }

    @GetMapping("/")
    public ResponseResult<Slice<UserListResponse>> getAllUsers(
            @RequestParam(defaultValue = "") String keyword,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return new ResponseResult<>(ResponseCode.SUCCESS, userService.getUserListSearch(keyword, pageable));
    }

    @GetMapping("/me")
    public ResponseResult<UserProfileResponse> getUserById() {
        Long userId = SecurityContextUtils.getAccountId();
        return new ResponseResult<>(ResponseCode.SUCCESS, userService.getUserById(userId));
    }

    @PutMapping("/me/account-type")
    public ResponseResult<Void> updateAccountType(@RequestBody @Valid UserUpdateAccountTypeRequest request) {
        Long userId = SecurityContextUtils.getAccountId();
        userService.updateAccountType(userId, request);
        return new ResponseResult<>(ResponseCode.SUCCESS);
    }

    @PutMapping("/me/profile")
    public ResponseResult<UserProfileResponse> updateProfile(@RequestBody @Valid UserUpdateProfileRequest request) {
        Long userId = SecurityContextUtils.getAccountId();
        return new ResponseResult<>(ResponseCode.SUCCESS, userService.updateProfile(userId, request));
    }

    @GetMapping("/me/activity-platform")
    public ResponseResult<UserActivityPlatformResponse> getActivityPlatform() {
        Long userId = SecurityContextUtils.getAccountId();
        return new ResponseResult<>(ResponseCode.SUCCESS, userService.getActivityPlatform(userId));
    }

    @PutMapping("/me/activity-platform")
    public ResponseResult<UserActivityPlatformResponse> updateActivityPlatform(@RequestBody @Valid UserUpdateActivityPlatformRequest request) {
        Long userId = SecurityContextUtils.getAccountId();
        return new ResponseResult<>(ResponseCode.SUCCESS, userService.updateActivityPlatform(userId, request));
    }

    @PutMapping("/me/email")
    public ResponseResult<UserUpdateEmailResponse> updateEmail(@RequestBody @Valid UserUpdateEmailRequest request) {
        Long userId = SecurityContextUtils.getAccountId();
        return new ResponseResult<>(ResponseCode.SUCCESS, userService.updateEmail(userId, request));
    }

    @PutMapping("/me/password")
    public ResponseResult<UserUpdatePasswordResponse> updatePassword(@RequestBody @Valid UserUpdatePasswordRequest request) {
        Long userId = SecurityContextUtils.getAccountId();
        return new ResponseResult<>(ResponseCode.SUCCESS, userService.updatePassword(userId, request));
    }

    @DeleteMapping("/")
    public ResponseResult<Void> deleteUser() {
        Long userId = SecurityContextUtils.getAccountId();
        userService.deleteUser(userId);
        return new ResponseResult<>(ResponseCode.SUCCESS);
    }
}
