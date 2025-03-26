package com.sing4u.kr.user.ui;

import com.sing4u.kr.common.response.PagingResponse;
import com.sing4u.kr.user.application.command.DeleteUserUseCase;
import com.sing4u.kr.user.application.command.UpdateEmailUseCase;
import com.sing4u.kr.user.application.command.UpdateNicknameUseCase;
import com.sing4u.kr.user.application.command.UpdatePasswordUseCase;
import com.sing4u.kr.user.application.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UpdateNicknameUseCase updateNicknameUseCase;
    private final UpdateEmailUseCase updateEmailUseCase;
    private final UpdatePasswordUseCase updatePasswordUseCase;
    private final DeleteUserUseCase deleteUserUseCase;

    @GetMapping
    public ResponseEntity<PagingResponse<UserResponse>> getUserList() {
        return null;
    }

    @GetMapping("/info")
    public ResponseEntity<UserResponse> getMyInfo() {
        return null;
    }

    @PatchMapping("/nickname")
    public ResponseEntity<Void> updateNickname() {
        return null;
    }

    @PatchMapping("/email")
    public ResponseEntity<Void> updateEmail() {
        return null;
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> updatePassword() {
        return null;
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser() {
        return null;
    }
}
