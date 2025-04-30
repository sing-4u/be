package com.sing4u.kr.application.exceptions;

import com.sing4u.kr.common.enums.ResponseCode;
import com.sing4u.kr.common.exception.ApiException;

public class NoAuthorizedException extends ApiException {
    public NoAuthorizedException() {
        super(ResponseCode.ERROR_NO_AUTHORIZED.getCode(), ResponseCode.ERROR_NO_AUTHORIZED.getMessage());
    }
}
