package com.sing4u.kr.user.ui;

import com.sing4u.kr.user.application.command.CreateUserUseCase;
import com.sing4u.kr.user.application.command.UpdateUserUseCase;
import com.sing4u.kr.user.application.dto.UserCommand;
import com.sing4u.kr.user.application.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUserList() {
        return null;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserCommand userCommand) {
        return ResponseEntity.ok(createUserUseCase.execute(userCommand));
    }

    @GetMapping
    public ResponseEntity<UserResponse> emailCheck(@RequestBody UserCommand userCommand) {
        return null;
    }

    @PatchMapping
    public ResponseEntity<UserResponse> updateEmail(@RequestBody UserCommand userCommand) {
        return null;
    }

    @PatchMapping
    public ResponseEntity<UserResponse> updatePassword(@RequestBody UserCommand userCommand) {
        return null;
    }

    @DeleteMapping
    public ResponseEntity<UserResponse> deleteUser() {
        return null;
    }
}
