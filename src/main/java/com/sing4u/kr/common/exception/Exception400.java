package com.sing4u.kr.common.exception;

import com.sing4u.kr.common.enums.ResponseCode;

public class Exception400 extends ApiException{
    public Exception400(ResponseCode responseCode) {
        super(responseCode);
    }

    public Exception400(String message, ResponseCode responseCode) {
        super(message, responseCode.getCode(), responseCode.getMessage());
    }
}
