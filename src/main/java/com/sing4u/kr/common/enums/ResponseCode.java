package com.sing4u.kr.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {
    SUCCESS("0001", "성공"),

    //
    //  서버 기본 에러
    //
    ERROR_INTERNAL_SERVER("1000", "서버 내부 오류입니다."),
    ERROR_WRONG_PARAMETERS("1001", "Wrong Parameters"),
    ERROR_NO_DATA("1002", "해당 데이터가 없거나, 이미 삭제되었습니다."),
    ERROR_DATA_EXISTED("1003", "이미 관련 데이터가 존재합니다."),

    ERROR_NO_AUTHORIZED("2000", "권한이 없습니다."),
    ERROR_INVALID_TOKEN("2004", "유효하지 않은 토큰입니다."),
    ERROR_EXPIRED_TOKEN("2005", "만료된 토큰입니다."),
    ERROR_TOKEN_OWNER_MISS_MATCH("2011", "올바른 토큰 소유자가 아닙니다."),
    ERROR_EXPIRED_OTP("2012", "만료된 OTP입니다."),

    ERROR_ALREADY_EXIST_USER("3000", "이미 존재하는 사용자 입니다."),
    ERROR_USER_NOT_FOUND("3001", "존재하지 않는 사용자 입니다"),

    ERROR_PASSWORD_MISMATCH("3002", "비밀번호가 일치하지 않습니다."),

    PASSWORD_RESET_RESEND_THROTTLED ("1201", "Too Many Requests: 잠시 후 다시 시도해 주세요."),
    PASSWORD_RESET_CODE_EXPIRED      ("1202", "인증번호 유효 시간이 만료되었습니다."),
    PASSWORD_RESET_CODE_INVALID      ("1203", "인증번호가 올바르지 않습니다."),
    PASSWORD_RESET_TOKEN_INVALID     ("1204", "인증이 만료되었거나 유효하지 않습니다."),
    PASSWORD_RESET_SOCIAL_ACCOUNT    ("1205", "SNS로 간편 가입된 계정입니다.")

    ;
    private final String code;
    private final String message;
}
