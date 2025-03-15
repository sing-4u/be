package com.sing4u.kr.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String code;
    private final String message;

    public ApiException(ExceptionCode exceptionCode) {
        super(exceptionCode.getDefaultMessage());
        this.status = exceptionCode.getStatus();
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getDefaultMessage();
    }

    public ApiException(ExceptionCode exceptionCode, String customMessage) {
        super(customMessage);
        this.status = exceptionCode.getStatus();
        this.code = exceptionCode.getCode();
        this.message = customMessage != null ? customMessage : exceptionCode.getDefaultMessage();
    }
}
