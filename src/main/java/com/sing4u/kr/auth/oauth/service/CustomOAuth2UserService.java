package com.sing4u.kr.auth.oauth.service;

import com.sing4u.kr.auth.dto.CustomUserPrincipal;
import com.sing4u.kr.auth.oauth.dto.GoogleUserInfo;
import com.sing4u.kr.auth.oauth.dto.OAuth2UserInfo;
import com.sing4u.kr.user.entity.User;
import com.sing4u.kr.user.entity.enums.SocialType;
import com.sing4u.kr.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder; // PasswordEncoder import
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화기 주입

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("oAuth2User : "+ oAuth2User);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo oAuth2Response;
        if(registrationId.equals("google")) {
            oAuth2Response = new GoogleUserInfo(oAuth2User.getAttributes());
        } else{
            // 다른 소셜 로그인 추가 시 여기에 구현
            throw new OAuth2AuthenticationException("Unsupported provider : " + registrationId);
        }

        User user = userRepository.findByEmailAndDeletedAtIsNull(oAuth2Response.getEmail())
                .orElseGet(() -> {
                    // 새로운 사용자일 경우 DB에 저장.
                    // 소셜 로그인 사용자는 비밀번호가 없으므로, 임의의 값을 암호화하여 저장.
                    String randomPassword = passwordEncoder.encode(java.util.UUID.randomUUID().toString());
                    return userRepository.save(
                            User.ofOAuth2(
                                    oAuth2Response.getEmail(),
                                    oAuth2Response.getName(),
                                    randomPassword,
                                    SocialType.valueOf(registrationId.toUpperCase())
                            )
                    );
                });

        userRepository.save(user);

        return new CustomUserPrincipal(user, oAuth2User.getAttributes());
    }
}