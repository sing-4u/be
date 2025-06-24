package com.sing4u.kr.health.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import java.util.List;

import com.sing4u.kr.application.exceptions.NoAuthorizedException;
import com.sing4u.kr.common.exception.Exception400;
import com.sing4u.kr.health.dto.SwaggerAuthResponse;
import com.sing4u.kr.jwt.provider.JwtTokenProvider;
import com.sing4u.kr.user.entity.User;
import com.sing4u.kr.user.entity.enums.UserType;
import com.sing4u.kr.user.enums.UserRole;
import com.sing4u.kr.user.repository.UserRepository;

import static com.sing4u.kr.common.enums.ResponseCode.ERROR_USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class SwaggerService {
    @Value("${spring.profiles.active}")
    private String activeProfile;

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public SwaggerAuthResponse swaggerAuthorization(String username, String password) {
        if (!(StringUtils.equals(activeProfile, "dev") ||
                StringUtils.equals(activeProfile, "local") || StringUtils.equals(activeProfile, "staging"))) {
            throw new NoAuthorizedException();
        }

        if (StringUtils.isEmpty(username)) {
            throw new NoAuthorizedException();
        }

        if (StringUtils.isEmpty(password)) {
            throw new NoAuthorizedException();
        }

        if (!StringUtils.equals(username, password)) {
            throw new NoAuthorizedException();
        }

        String[] type = StringUtils.split(username, "_");
        UserRole mainRole = UserRole.getMainAccountRoleFromString(List.of(type[0]));
        Long accountId = Long.parseLong(type[1]);
        log.info("accountId : " + accountId);
        User user = this.userRepository.findByIdAndDeletedAtIsNull(accountId)
                .orElseThrow(() -> new Exception400(ERROR_USER_NOT_FOUND));

        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(),
                List.of(mainRole),
                user.getNickname()
        );

        return SwaggerAuthResponse.of(
                accessToken,
                accessToken
        );
    }
}
