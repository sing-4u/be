package com.sing4u.kr.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;


import java.net.URISyntaxException;
import java.util.stream.Collectors;

import com.sing4u.kr.common.dto.ResponseResult;
import com.sing4u.kr.common.enums.ResponseCode;
import com.sing4u.kr.jwt.exceptions.ExpiredTokenException;
import com.sing4u.kr.jwt.exceptions.InvalidTokenException;

import static com.sing4u.kr.common.enums.ResponseCode.ERROR_INTERNAL_SERVER;
import static com.sing4u.kr.common.enums.ResponseCode.ERROR_WRONG_PARAMETERS;

@SuppressWarnings("rawtypes")
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            IllegalStateException.class,
            ConstraintViolationException.class,
            BindException.class,
            MethodArgumentTypeMismatchException.class,
            HttpRequestMethodNotSupportedException.class,
            NumberFormatException.class,
            URISyntaxException.class,
            MissingServletRequestParameterException.class,
            MissingServletRequestPartException.class
    })
    public ResponseResult handleWrongParametersException(Exception e) {
        log.warn("WrongParametersException: ", e);
        return new ResponseResult<>(ERROR_WRONG_PARAMETERS.getCode(), ERROR_WRONG_PARAMETERS.getMessage(), null);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseResult handleAccessDeniedException(AccessDeniedException e) {
        log.error("AccessDeniedException: ", e);
        return new ResponseResult<>(ResponseCode.ERROR_NO_AUTHORIZED.getCode(), ResponseCode.ERROR_NO_AUTHORIZED.getMessage(), null);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = InsufficientAuthenticationException.class)
    public ResponseResult handleInsufficientAuthenticationException(InsufficientAuthenticationException e) {
        log.warn("InsufficientAuthenticationException: ", e);
        return new ResponseResult<>(ResponseCode.ERROR_INVALID_TOKEN.getCode(), ResponseCode.ERROR_INVALID_TOKEN.getMessage(), null);
    }

    /**
     * 지정된 권한을 통과하지 못했을 때
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = AuthenticationCredentialsNotFoundException.class)
    public ResponseResult handleAuthenticationCredentialsNotFoundException(
            AuthenticationCredentialsNotFoundException e) {
        log.error("AuthenticationCredentialsNotFoundException: ", e);
        return new ResponseResult<>(ResponseCode.ERROR_NO_AUTHORIZED.getCode(), ResponseCode.ERROR_NO_AUTHORIZED.getMessage(), null);
    }

    /**
     * 권한 및 토큰 관련 에러
     *
     * @return ResponseObject
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = {InvalidTokenException.class})
    public ResponseResult handleNoAuthorizedException(ApiException e) {
        log.error("AuthorizedException: ", e);
        return new ResponseResult<>(e.getCode(), e.getMessage(), null);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = {ExpiredTokenException.class})
    public ResponseResult handleExpiredTokenException(ExpiredTokenException e) {
        log.warn("ExpiredTokenException: ", e);
        return new ResponseResult<>(e.getCode(), e.getMessage(), null);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {
            Exception400.class,
    })
    public ResponseResult handleException400(ApiException e) {
        log.error("", e);
        return new ResponseResult<>(e.getCode(), e.getMessage(), null);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(value = {
            Exception409.class,
    })
    public ResponseResult handleException409(ApiException e) {
        log.error("", e);
        return new ResponseResult<>(e.getCode(), e.getMessage(), null);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseResult handleRuntimeException(RuntimeException e) {
        log.error("", e);

        return new ResponseResult<>(ERROR_INTERNAL_SERVER.getCode(), ERROR_INTERNAL_SERVER.getMessage(), null);
    }
}
