package com.sing4u.kr.user.controller;

import com.sing4u.kr.user.dto.request.UserCreateRequest;
import com.sing4u.kr.user.dto.request.UserUpdateRequest;
import com.sing4u.kr.user.dto.response.UserCreateResponse;
import com.sing4u.kr.user.dto.response.UserDetailResponse;
import com.sing4u.kr.user.dto.response.UserListResponse;
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
    public ResponseEntity<UserDetailResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}/nickname")
    public ResponseEntity<UserDetailResponse> updateNickname(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateNickname(id, request));
    }

    @PutMapping("/{id}/email")
    public ResponseEntity<UserDetailResponse> updateEmail(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateEmail(id, request));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<UserDetailResponse> updatePassword(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updatePassword(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
