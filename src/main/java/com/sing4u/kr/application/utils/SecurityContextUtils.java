package com.sing4u.kr.application.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

import com.sing4u.kr.application.model.DefaultUserDetail;
import com.sing4u.kr.common.enums.ResponseCode;
import com.sing4u.kr.common.exception.ApiException;
import com.sing4u.kr.user.enums.UserRole;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

@UtilityClass
@Slf4j
public class SecurityContextUtils {
    public static void setSecurityContext(Long accountId,String email, List<UserRole> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        for (UserRole role : emptyIfNull(roles)) {
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(toGrantedAuthority(role));
            authorities.add(simpleGrantedAuthority);
        }

        log.info("setSecurityContext: accountId={}, roles={}", accountId, authorities);

        DefaultUserDetail userDetail = DefaultUserDetail.of(accountId,email, authorities);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private String toGrantedAuthority(UserRole role) {
        return String.format("ROLE_%s", role);
    }

    public Long getAccountId(Authentication authentication) {

        if (authentication == null) {
            throw new ApiException(ResponseCode.ERROR_NO_AUTHORIZED);
        }

        if (!DefaultUserDetail.class.isInstance(authentication.getPrincipal())) {
            throw new ApiException(ResponseCode.ERROR_NO_AUTHORIZED);
        }

        DefaultUserDetail customUserDetail = DefaultUserDetail.class.cast(authentication.getPrincipal());
        return customUserDetail.getId();
    }

    public Long getAccountId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new ApiException(ResponseCode.ERROR_NO_AUTHORIZED);
        }

        if (!DefaultUserDetail.class.isInstance(authentication.getPrincipal())) {
            throw new ApiException(ResponseCode.ERROR_NO_AUTHORIZED);
        }

        DefaultUserDetail customUserDetail = DefaultUserDetail.class.cast(authentication.getPrincipal());
        return customUserDetail.getId();
    }

    public String getEmail() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof DefaultUserDetail userDetail)) {
            throw new ApiException(ResponseCode.ERROR_NO_AUTHORIZED);
        }

        return userDetail.getEmail();
    }

}
