package com.sing4u.kr.jwt.exceptions;

import com.sing4u.kr.common.enums.ResponseCode;
import com.sing4u.kr.common.exception.ApiException;

public class ExpiredTokenException extends ApiException {
    public ExpiredTokenException() {
        super(ResponseCode.ERROR_EXPIRED_TOKEN.getCode(), ResponseCode.ERROR_EXPIRED_TOKEN.getMessage());
    }

    public ExpiredTokenException(String message) {
        super(message, ResponseCode.ERROR_EXPIRED_TOKEN.getCode(), ResponseCode.ERROR_EXPIRED_TOKEN.getMessage());
    }
}
