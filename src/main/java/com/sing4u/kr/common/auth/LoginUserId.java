package com.sing4u.kr.common.auth;

import io.swagger.v3.oas.annotations.Parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER) //적용 대상
@Retention(RetentionPolicy.RUNTIME) // 보존 정책
@Parameter(hidden = true)
public @interface LoginUserId {}
