package com.sing4u.kr.auth.service;

import com.sing4u.kr.auth.dto.response.SendCodeResponse;
import com.sing4u.kr.auth.entity.RefreshToken;
import com.sing4u.kr.auth.dto.LoginDto;
import com.sing4u.kr.auth.dto.request.LoginRequest;
import com.sing4u.kr.auth.dto.response.TokenDto;
import com.sing4u.kr.auth.event.PasswordResetCodeIssuedEvent;
import com.sing4u.kr.auth.mail.PasswordResetMailer;
import com.sing4u.kr.auth.repository.PasswordResetTokenRepository;
import com.sing4u.kr.auth.repository.RefreshTokenRepository;
import com.sing4u.kr.common.enums.ResponseCode;
import com.sing4u.kr.common.exception.Exception400;
import com.sing4u.kr.jwt.exceptions.InvalidTokenException;
import com.sing4u.kr.jwt.model.JwtToken;
import com.sing4u.kr.jwt.provider.JwtTokenProvider;
import com.sing4u.kr.mail.service.MailService;
import com.sing4u.kr.user.entity.User;
import com.sing4u.kr.user.entity.enums.UserType;
import com.sing4u.kr.user.enums.UserRole;
import com.sing4u.kr.user.entity.enums.SocialType;
import com.sing4u.kr.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokens;        // Caffeine 저장소 (code/throttle/ticket)
    private final PasswordResetMailer passwordResetMailer;                               // SMTP 메일 발송
    private final ApplicationEventPublisher publisher;

    // 프론트 타이머 표시용(값만 응답에 내려줌. 실제 TTL은 Caffeine Bean에서 관리)
    private static final int CODE_EXPIRES_SECONDS = 180;   // 3분
    private static final int RESEND_THROTTLE_SECONDS = 30; // 30초

    @Transactional
    public LoginDto emailLogin(LoginRequest request) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(() -> new Exception400(ResponseCode.ERROR_USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new Exception400(ResponseCode.ERROR_WRONG_PARAMETERS);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(),
                user.getEmail(),
                List.of(user.getRole()),
                user.getNickname()
        );

        String refreshTokenValue = jwtTokenProvider.generateRefreshToken(
                user.getId(),
                user.getEmail(),
                List.of(user.getRole()),
                user.getNickname()
        );

        refreshTokenRepository.save(RefreshToken.of(user.getId(), refreshTokenValue));
        return LoginDto.of(accessToken, refreshTokenValue, user.getProfileImage());
    }

    @Transactional
    public TokenDto refresh(String refreshToken) {
        // 전체적인 재발급 로직 변경 (Refresh Token Rotaion 적용)
        // 1. 요청으로 받은 Refresh Token 유효성 검증
        JwtToken jwt;
        try {
            jwt = jwtTokenProvider.getAllClaimsFromToken(refreshToken);
        } catch (Exception e) {
            throw new InvalidTokenException();
        }

        // 2. DB에서 해당 Refresh Token 조회
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> {
                    // DB에 토큰이 없다는 것은 이미 사용되었거나 탈취 후 삭제되었을 가능성이 있음
                    // 로그를 남기고 예외 처리
                    log.warn("Invalid or already used refresh token: {}", refreshToken);
                    return new InvalidTokenException();
                });

        // 3. 기존 Refresh Token을 DB에서 삭제 (재사용 방지)
        refreshTokenRepository.deleteByToken(refreshToken);

        // 4. 새로운 Access Token과 Refresh Token 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(
                jwt.getUserId(),
                jwt.getEmail(),
                jwt.getRoles(),
                jwt.getNickName()
        );
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(
                jwt.getUserId(),
                jwt.getEmail(),
                jwt.getRoles(),
                jwt.getNickName()
        );

        // 5. 새로 생성된 Refresh Token을 DB에 저장
        refreshTokenRepository.save(RefreshToken.of(jwt.getUserId(), newRefreshToken));

        return TokenDto.of(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    /**
     * 인증번호 전송: 6자리 코드 생성 → Caffeine에 3분 저장, 30초 재요청 제한 → 이메일 발송
     */
    @Transactional(readOnly = true)
    public SendCodeResponse sendPasswordResetCode(String email) {
        // 가입 여부 확인
        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new Exception400(ResponseCode.ERROR_USER_NOT_FOUND));

        // 소셜 전용 계정 차단
        if (user.getSocialType() != null && user.getSocialType() != SocialType.LOCAL) {
            throw new Exception400(ResponseCode.PASSWORD_RESET_SOCIAL_ACCOUNT);
        }

        // 30초 재요청 제한
        if (passwordResetTokens.isThrottled(email)) {
            throw new Exception400(ResponseCode.PASSWORD_RESET_RESEND_THROTTLED);
        }

        // 6자리 숫자 코드
        String code = String.format("%06d", ThreadLocalRandom.current().nextInt(0, 1_000_000));

        // 저장 (TTL/만료는 Caffeine Bean 설정)
        passwordResetTokens.saveCode(email, code);
        passwordResetTokens.throttle(email);

        // 메일 발송(비동기 이벤트)
        publisher.publishEvent(new PasswordResetCodeIssuedEvent(email, code));

        return new SendCodeResponse(CODE_EXPIRES_SECONDS, RESEND_THROTTLE_SECONDS, maskEmail(email));
    }

    /**
     * 인증번호 검증: 일치하면 일회성 resetToken 발급(10분 TTL), 코드 즉시 폐기
     */
    @Transactional(readOnly = true)
    public String verifyPasswordResetCode(String email, String code) {
        String stored = passwordResetTokens.getCode(email);
        if (stored == null) {
            // 만료되었거나 발송 이력이 없음
            throw new Exception400(ResponseCode.PASSWORD_RESET_CODE_EXPIRED);
        }
        if (!stored.equals(code)) {
            // 불일치
            throw new Exception400(ResponseCode.PASSWORD_RESET_CODE_INVALID );
        }

        // 1회성 사용: 코드 삭제
        passwordResetTokens.removeCode(email);

        // resetToken 발급(난수) 및 저장(10분 TTL은 Bean에서)
        String resetToken = UUID.randomUUID().toString();
        passwordResetTokens.saveTicket(email, resetToken);
        return resetToken;
    }

    /**
     * 최종 비밀번호 변경: resetToken 검증 → 사용자 조회 → 암호화 저장 → 토큰 폐기
     */
    @Transactional
    public void confirmPasswordReset(String email, String resetToken, String newPassword) {
        String stored = passwordResetTokens.getTicket(email);
        if (stored == null || !stored.equals(resetToken)) {
            throw new Exception400(ResponseCode.PASSWORD_RESET_TOKEN_INVALID);
        }

        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new Exception400(ResponseCode.ERROR_USER_NOT_FOUND));

        user.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // 토큰 1회성 사용 후 폐기
        passwordResetTokens.removeTicket(email);
    }

    private String maskEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 1) return "***";
        return email.charAt(0) + "***" + email.substring(at);
    }
}
