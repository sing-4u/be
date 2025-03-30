package com.sing4u.kr.common.security;


import com.sing4u.kr.user.application.command.GoogleLoginUseCase;
import com.sing4u.kr.user.application.command.GoogleSignupUseCase;
import com.sing4u.kr.user.domain.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final GoogleLoginUseCase googleLoginUseCase;
    private final GoogleSignupUseCase googleSignupUseCase;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String nickname = oAuth2User.getAttribute("name");

        String token;
        if (userRepository.findByEmail(email).isPresent()) {
            token = googleLoginUseCase.execute(email);
        } else {
            token = googleSignupUseCase.execute(email, nickname);
        }

        response.setHeader("Authorization", "Bearer " + token);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
