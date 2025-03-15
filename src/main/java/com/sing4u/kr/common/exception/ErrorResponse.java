package com.sing4u.kr.common.exception;

public record ErrorResponse(String code, String message, int status) {

    public static ErrorResponse of(ApiException ex) {
        return new ErrorResponse(ex.getCode(), ex.getMessage(), ex.getStatus().value());
    }

    public static ErrorResponse of(ExceptionCode exceptionCode) {
        return new ErrorResponse(exceptionCode.getCode(), exceptionCode.getDefaultMessage(), exceptionCode.getStatus().value());
    }
}
