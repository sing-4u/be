package com.sing4u.kr.common.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;


import java.util.HashMap;
import java.util.Map;

import com.sing4u.kr.common.enums.ResponseCode;

import static com.sing4u.kr.common.enums.ResponseCode.*;
import static com.sing4u.kr.common.properties.GlobalProperties.HEADER_EXCEPTION_CODE;
import static com.sing4u.kr.common.utils.ObjectMapperUtils.defaultObjectMapper;

public class AbstractAuthExceptionHandler {
    public static final ResponseCode[] EXCEPTION_CODES = new ResponseCode[] {ERROR_EXPIRED_TOKEN, ERROR_INVALID_TOKEN};
    public static final ResponseCode DEFAULT_AUTH_EXCEPTION_CODE = ERROR_NO_AUTHORIZED;

    public static ResponseCode getResponseCode(String code, ResponseCode defaultValue, RuntimeException runtimeException) {
        for (ResponseCode responseCode : EXCEPTION_CODES) {
            if (StringUtils.equalsIgnoreCase(code, responseCode.getCode())) {
                return responseCode;
            }
        }

        String cause = runtimeException.getMessage();

        if (StringUtils.contains(cause,"Access token expired")) {
            return ERROR_EXPIRED_TOKEN;
        } else if(StringUtils.contains(cause, "Cannot convert access token to JSON")) {
            return ERROR_INVALID_TOKEN;
        }

        return defaultValue;
    }

    protected void doDefault(HttpServletResponse response, AccessDeniedException accessDeniedException) throws Exception {
        ResponseCode responseCode = getResponseCode(response.getHeader(HEADER_EXCEPTION_CODE), DEFAULT_AUTH_EXCEPTION_CODE, accessDeniedException);
        writeResponse(response, responseCode);
    }

    protected void doDefault(HttpServletResponse response, AuthenticationException authException) throws Exception {
        ResponseCode responseCode = getResponseCode(response.getHeader(HEADER_EXCEPTION_CODE), DEFAULT_AUTH_EXCEPTION_CODE, authException);
        writeResponse(response, responseCode);
    }

    private void writeResponse(HttpServletResponse response, ResponseCode responseCode) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("code", responseCode.getCode());
        map.put("message", responseCode.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(defaultObjectMapper.writeValueAsString(map));
    }
}
