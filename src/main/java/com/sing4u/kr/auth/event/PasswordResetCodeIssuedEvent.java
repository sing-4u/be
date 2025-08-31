package com.sing4u.kr.auth.event;

public record PasswordResetCodeIssuedEvent(String email, String code) { }
