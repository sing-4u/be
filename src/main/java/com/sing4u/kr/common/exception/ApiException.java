package com.sing4u.kr.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


import com.sing4u.kr.common.enums.ResponseCode;

@Getter
public class ApiException extends RuntimeException {

    private final String code;
    private final String message;

    public ApiException(ResponseCode responseCode) {
        this(responseCode.getCode(), responseCode.getMessage());
    }

    public ApiException(ResponseCode responseCode, String message) {
        super(message);
        this.code = responseCode.getCode();
        this.message = message;
    }

    public ApiException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public ApiException(String message, String code, String desc) {
        super(message);
        this.code = code;
        this.message = desc;
    }

    public ApiException(ExceptionCode exceptionCode, String customMessage) {
        super(customMessage);
        this.code = exceptionCode.getCode();
        this.message = customMessage != null ? customMessage : exceptionCode.getDefaultMessage();
    }

    public ApiException(ExceptionCode exceptionCode) {
        super(exceptionCode.getDefaultMessage());
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getDefaultMessage();
    }
}
