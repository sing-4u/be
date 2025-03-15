package com.sing4u.kr.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {

    // 공통 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "An unexpected error occurred."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Invalid request."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "Resource not found."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Validation failed."),
    CONFLICT(HttpStatus.CONFLICT, "CONFLICT", "Conflict in resource state."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "Access denied."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Authentication failed."),
    
    // 도메인별 에러
    CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "CUSTOMER_NOT_FOUND", "Customer not found."),
    ORDER_CANNOT_BE_CANCELED(HttpStatus.CONFLICT, "ORDER_CANNOT_BE_CANCELED", "Order cannot be canceled.");

    private final HttpStatus status;
    private final String code;
    private final String defaultMessage;

    ExceptionCode(HttpStatus status, String code, String defaultMessage) {
        this.status = status;
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
}
