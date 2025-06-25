package com.sing4u.kr.auth.utils;

import com.sing4u.kr.auth.model.CustomOAuth2User;
import com.sing4u.kr.auth.service.CustomOAuth2UserService;
import com.sing4u.kr.jwt.provider.JwtTokenProvider;
import com.sing4u.kr.user.entity.User;
import com.sing4u.kr.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        CustomOAuth2User customUser = (CustomOAuth2User) authentication.getPrincipal();
        User user = customUser.getUser();

//        if (user == null) {
//            // 별도 정보 입력
//            response.sendRedirect("http://localhost:3000/oauth/signup");
//            return;
//        }

        String token = jwtTokenProvider.generateAccessToken(
                user.getId(),
                List.of(user.getRole()),
                user.getNickname(),
                user.getUserType()
        );

        response.sendRedirect("http://localhost:3000/oauth?token=" + token);
    }
}
