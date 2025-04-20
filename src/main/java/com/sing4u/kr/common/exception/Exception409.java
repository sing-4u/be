package com.sing4u.kr.common.exception;

import com.sing4u.kr.common.enums.ResponseCode;

public class Exception409 extends ApiException {
    public Exception409(ResponseCode responseCode) {
        super(responseCode);
    }

    public Exception409(ResponseCode responseCode, String message) {
        super(responseCode, message);
    }
}
