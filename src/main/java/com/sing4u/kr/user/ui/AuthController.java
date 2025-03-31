package com.sing4u.kr.user.ui;

import com.sing4u.kr.user.application.command.*;
import com.sing4u.kr.user.application.dto.LoginCommand;
import com.sing4u.kr.user.application.dto.TokenResponse;
import com.sing4u.kr.user.application.dto.UserCommand;
import com.sing4u.kr.user.infra.GoogleOAuthClient;
import com.sing4u.kr.user.infra.GoogleOAuthUserInfo;
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
    private final GoogleOAuthClient googleOAuthClient;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody UserCommand command) {
        signupUseCase.execute(command);
        return ResponseEntity.ok().build();
    }

/*    @PostMapping("/signup/google")
    public ResponseEntity<TokenResponse> googleSignup(@RequestBody GoogleTokenRequest request) {
        GoogleOAuthUserInfo userInfo = googleOAuthClient.getUserInfo(request.accessToken());
        String token = googleSignupUseCase.execute(userInfo.email(), userInfo.name());
        return ResponseEntity.ok(new TokenResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginCommand command) {
        TokenResponse token = loginUseCase.execute(command);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login/google")
    public ResponseEntity<TokenResponse> googleLogin(@RequestBody GoogleTokenRequest request) {
        GoogleOAuthUserInfo userInfo = googleOAuthClient.getUserInfo(request.accessToken());
        String token = googleLoginUseCase.execute(userInfo.email());
        return ResponseEntity.ok(new TokenResponse(token));
    }

    @PostMapping("/password/reset-code")
    public ResponseEntity<Void> sendResetCode() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/verify-code")
    public ResponseEntity<Void> verifyResetCode() {
        return ResponseEntity.ok().build();
    }*/
}
