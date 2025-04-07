package com.sing4u.kr.user.ui;

import com.sing4u.kr.common.security.JwtTokenProvider;
import com.sing4u.kr.user.application.command.*;
import com.sing4u.kr.user.application.dto.LoginCommand;
import com.sing4u.kr.user.application.dto.TokenResponse;
import com.sing4u.kr.user.application.dto.UserCommand;
import com.sing4u.kr.user.domain.User;
import com.sing4u.kr.user.domain.UserRepository;
import com.sing4u.kr.user.infra.GoogleOAuthClient;
import com.sing4u.kr.user.infra.GoogleOAuthUserInfo;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

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
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

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

    @PostMapping("/token/refresh")
    public ResponseEntity<TokenResponse> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // AccessToken은 무조건 새로 발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(email);

        // 만료까지 3일 이하 남으면 RefreshToken도 재발급
        Date refreshExpiration = jwtTokenProvider.getExpiration(refreshToken);
        Duration remaining = Duration.between(Instant.now(), refreshExpiration.toInstant());

        String finalRefreshToken = refreshToken;
        if (remaining.compareTo(Duration.ofDays(3)) <= 0) {
            finalRefreshToken = jwtTokenProvider.generateRefreshToken(email);

            // 새 쿠키로 내려줌
            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", finalRefreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(Duration.ofDays(7))
                    .sameSite("Strict")
                    .build();
            response.setHeader("Set-Cookie", refreshCookie.toString());
        }

        return ResponseEntity.ok(new TokenResponse(newAccessToken, null)); // refreshToken은 쿠키로만 전달
    }
}
