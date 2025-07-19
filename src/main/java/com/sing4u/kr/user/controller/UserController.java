package com.sing4u.kr.user.controller;

import com.sing4u.kr.application.utils.SecurityContextUtils;
import com.sing4u.kr.common.auth.LoginUserId;
import com.sing4u.kr.common.dto.ResponseResult;
import com.sing4u.kr.common.enums.ResponseCode;
import com.sing4u.kr.user.dto.request.*;
import com.sing4u.kr.user.dto.response.*;
import com.sing4u.kr.user.service.UserService;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @PostMapping("/register")
    public ResponseResult<UserCreateResponse> createUser(@RequestBody @Valid UserCreateRequest request) {
        return new ResponseResult<>(ResponseCode.SUCCESS, userService.createUser(request));
    }

    @Operation(summary = "내 정보 조회", description = "로그인된 사용자의 상세 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseResult<UserProfileResponse> getUserById(@LoginUserId Long userId) {
        return new ResponseResult<>(ResponseCode.SUCCESS, userService.getUserById(userId));
    }

    @Operation(summary = "내 계정 유형 변경", description = "FAN 또는 ARTIST로 계정 유형을 변경합니다.")
    @PutMapping("/me/account-type")
    public ResponseResult<Void> updateAccountType(@LoginUserId Long userId, @RequestBody @Valid UserUpdateAccountTypeRequest request) {
        userService.updateAccountType(userId, request);
        return new ResponseResult<>(ResponseCode.SUCCESS);
    }

    @Operation(summary = "내 프로필 수정", description = "닉네임, 소개, 프로필 이미지 등 프로필 정보를 수정합니다.")
    @PutMapping("/me/profile")
    public ResponseResult<UserProfileResponse> updateProfile(@LoginUserId Long userId, @RequestBody @Valid UserUpdateProfileRequest request) {
        return new ResponseResult<>(ResponseCode.SUCCESS, userService.updateProfile(userId, request));
    }

    @Operation(summary = "이메일 변경", description = "로그인된 사용자의 이메일을 변경합니다. 현재 비밀번호 확인이 필요합니다.")
    @PutMapping("/me/email")
    public ResponseResult<UserUpdateEmailResponse> updateEmail(@LoginUserId Long userId, @RequestBody @Valid UserUpdateEmailRequest request) {
        return new ResponseResult<>(ResponseCode.SUCCESS, userService.updateEmail(userId, request));
    }

    @Operation(summary = "비밀번호 변경", description = "로그인된 사용자의 비밀번호를 변경합니다. 현재 비밀번호 확인이 필요합니다.")
    @PutMapping("/me/password")
    public ResponseResult<UserUpdatePasswordResponse> updatePassword(@LoginUserId Long userId, @RequestBody @Valid UserUpdatePasswordRequest request) {
        userService.updatePassword(userId, request);
        return new ResponseResult<>(ResponseCode.SUCCESS);
    }

    @Operation(summary = "회원 탈퇴", description = "로그인된 사용자의 계정을 삭제(soft-delete) 처리합니다.")
    @DeleteMapping("/")
    public ResponseResult<Void> deleteUser(@LoginUserId Long userId) {
        userService.deleteUser(userId);
        return new ResponseResult<>(ResponseCode.SUCCESS);
    }

    @Operation(summary = "프로필 이미지 변경", description = "사용자의 프로필 이미지를 업로드하고 변경합니다.")
    @PatchMapping(value = "/me/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseResult<UserUpdateProfileImageResponse> updateProfileImage(@LoginUserId Long userId, @RequestPart MultipartFile profileImage) {
        return new ResponseResult<>(ResponseCode.SUCCESS, userService.updateUserProfileImage(userId, profileImage));
    }

    @Operation(summary = "프로필 이미지 삭제", description = "사용자의 프로필 이미지를 삭제합니다.")
    @PatchMapping("/me/profile-image/delete")
    public ResponseResult<Void> deleteProfileImage(@LoginUserId Long userId) {
        userService.deleteUserProfileImage(userId);
        return new ResponseResult<>(ResponseCode.SUCCESS);
    }

}
