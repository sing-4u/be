package com.sing4u.kr.jwt.exceptions;

import com.sing4u.kr.common.enums.ResponseCode;
import com.sing4u.kr.common.exception.ApiException;

public class InvalidTokenException extends ApiException {
    public InvalidTokenException() {
        super(ResponseCode.ERROR_INVALID_TOKEN);
    }

    public InvalidTokenException(String message) {
        super(message, ResponseCode.ERROR_INVALID_TOKEN.getCode(), ResponseCode.ERROR_INVALID_TOKEN.getMessage());
    }
}
