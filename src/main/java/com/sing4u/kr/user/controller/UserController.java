package com.sing4u.kr.user.controller;

import com.sing4u.kr.user.dto.request.*;
import com.sing4u.kr.user.dto.response.*;
import com.sing4u.kr.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserCreateResponse> createUser(@RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @GetMapping
    public ResponseEntity<Slice<UserListResponse>> getAllUsers(
            @RequestParam(defaultValue = "") String keyword,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(userService.getUserListSearch(keyword, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}/accountType")
    public ResponseEntity<Void> updateAccountType(@PathVariable Long id, @RequestBody UserUpdateAccountTypeRequest request) {
        userService.updateAccountType(id, request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(@PathVariable Long id, @RequestBody UserUpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(id, request));
    }

    @PutMapping("/{id}/email")
    public ResponseEntity<UserUpdateEmailResponse> updateEmail(@PathVariable Long id, @RequestBody UserUpdateEmailRequest request) {
        return ResponseEntity.ok(userService.updateEmail(id, request));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<UserUpdatePasswordResponse> updatePassword(@PathVariable Long id, @RequestBody UserUpdatePasswordRequest request) {
        return ResponseEntity.ok(userService.updatePassword(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
