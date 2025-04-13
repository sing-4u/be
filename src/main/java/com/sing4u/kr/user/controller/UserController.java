package com.sing4u.kr.user.controller;

import com.sing4u.kr.user.dto.UserDto;
import com.sing4u.kr.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/nickname")
    public ResponseEntity<UserDto> updateNickname(@PathVariable Long id, @RequestBody UserDto dto) {
        return ResponseEntity.ok(userService.updateNickname(id, dto));
    }

    @PutMapping("/{id}/email")
    public ResponseEntity<UserDto> updateEmail(@PathVariable Long id, @RequestBody UserDto dto) {
        return ResponseEntity.ok(userService.updateEmail(id, dto));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<UserDto> updatePassword(@PathVariable Long id, @RequestBody UserDto dto) {
        return ResponseEntity.ok(userService.updatePassword(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
