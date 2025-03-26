package com.sing4u.kr.user.ui;

import com.sing4u.kr.user.application.command.*;
import com.sing4u.kr.user.application.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final SignupUseCase signupUseCase;
    private final GoogleSignupUseCase googleSignupUseCase;
    private final LoginUseCase loginUseCase;
    private final GoogleLoginUseCase googleLoginUseCase;
    private final SendResetCodeUseCase sendResetCodeUseCase;
    private final VerifyResetCodeUseCase verifyResetCodeUseCase;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup() {
        return null;
    }

    @PostMapping("/signup/google")
    public ResponseEntity<TokenResponse> googleSignup() {
        return null;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login() {
        return null;
    }

    @PostMapping("/login/google")
    public ResponseEntity<TokenResponse> googleLogin() {
        return null;
    }

    @PostMapping("/password/reset-code")
    public ResponseEntity<Void> sendResetCode() {
        return null;
    }

    @PostMapping("/password/verify-code")
    public ResponseEntity<Void> verifyResetCode() {
        return null;
    }
}
