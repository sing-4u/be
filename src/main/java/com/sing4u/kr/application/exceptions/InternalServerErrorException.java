package com.sing4u.kr.application.exceptions;

import com.sing4u.kr.common.enums.ResponseCode;
import com.sing4u.kr.common.exception.ApiException;

public class InternalServerErrorException extends ApiException {

    public InternalServerErrorException() {
        super(ResponseCode.ERROR_INTERNAL_SERVER.getCode(), ResponseCode.ERROR_INTERNAL_SERVER.getMessage());
    }

    public InternalServerErrorException(String message) {
        super(message, ResponseCode.ERROR_INTERNAL_SERVER.getCode(), ResponseCode.ERROR_INTERNAL_SERVER.getMessage());
    }
}
