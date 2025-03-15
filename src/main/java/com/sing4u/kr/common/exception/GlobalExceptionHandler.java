package com.sing4u.kr.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(ErrorResponse.of(ex));
    }

    // Validation 관련 스프링 내장 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorDetails = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ApiException apiException = new ApiException(ExceptionCode.VALIDATION_ERROR, errorDetails);
        return ResponseEntity
                .status(apiException.getStatus())
                .body(ErrorResponse.of(apiException));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ApiException apiException = new ApiException(ExceptionCode.INTERNAL_SERVER_ERROR, ex.getMessage());
        return ResponseEntity
                .status(apiException.getStatus())
                .body(ErrorResponse.of(apiException));
    }
}
