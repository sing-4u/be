package com.sing4u.kr.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;


import com.sing4u.kr.application.exceptions.InternalServerErrorException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestAccessDeniedHandler extends AbstractAuthExceptionHandler implements AccessDeniedHandler {

    /**
     * 인증된 사용자가 보호된 리소스에 접근할 때 호출되는 메소드
     *
     * @param request                HttpServletRequest
     * @param response               HttpServletResponse
     * @param accessDeniedException  AccessDeniedException
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) {
        try {
            doDefault(response, accessDeniedException);
        } catch (Exception e) {
            log.error("RestAccessDeniedHandler 에서 해당 Exception 정의가 잘못되었습니다. : " + e.getMessage());
            throw new InternalServerErrorException();
        }
    }
}
