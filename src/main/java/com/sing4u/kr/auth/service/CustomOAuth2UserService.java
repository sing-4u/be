package com.sing4u.kr.auth.service;

import com.sing4u.kr.auth.model.CustomOAuth2User;
import com.sing4u.kr.user.entity.User;
import com.sing4u.kr.user.entity.enums.SocialType;
import com.sing4u.kr.user.entity.enums.UserType;
import com.sing4u.kr.user.enums.UserRole;
import com.sing4u.kr.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId(); // "google", "kakao"
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String nickname = null; //임시 닉네임

        Optional<User> optionalUser = userRepository.findByEmailAndDeletedAtIsNull(email);

//        if (optionalUser.isEmpty()) {
//            // 회원가입 안 된 사용자 → 추가 정보 입력이 필요함
//            return new CustomOAuth2User(null, attributes); // User는 null
//        }

        User user = optionalUser.orElseGet(() -> {
            User newUser = User.builder()
                    .email(email)
                    .nickname("소셜유저_" + UUID.randomUUID().toString().substring(0, 8))
                    .role(UserRole.USER)
                    .userType(UserType.USER)
                    .socialType(SocialType.valueOf(provider.toUpperCase()))
                    .isOpen(false)
                    .build();
            return userRepository.save(newUser);
        });

        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }

}
