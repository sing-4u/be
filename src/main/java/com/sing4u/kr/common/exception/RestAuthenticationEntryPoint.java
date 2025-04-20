package com.sing4u.kr.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


import com.sing4u.kr.application.exceptions.InternalServerErrorException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint extends AbstractAuthExceptionHandler implements AuthenticationEntryPoint {
    /**
     * 인증되지 않은 사용자가 보호된 리소스에 접근할 때 호출되는 메소드
     *
     * @param request        HttpServletRequest
     * @param response       HttpServletResponse
     * @param authException  AuthenticationException
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        try {
            doDefault(response, authException);
        } catch (Exception e) {
            log.error("RestAuthenticationEntryPoint 에서 해당 Exception 정의가 잘못되었습니다. : " + e.getMessage());
            throw new InternalServerErrorException();
        }
    }
}
