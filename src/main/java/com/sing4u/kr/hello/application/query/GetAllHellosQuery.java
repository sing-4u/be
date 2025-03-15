package com.sing4u.kr.hello.application.query;

import com.sing4u.kr.common.exception.ApiException;
import com.sing4u.kr.common.exception.ExceptionCode;
import com.sing4u.kr.hello.domain.Hello;
import com.sing4u.kr.hello.domain.HelloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllHellosQuery {
    private final HelloRepository helloRepository;

    public List<Hello> execute() {
        List<Hello> result = helloRepository.findAll();

        if (result.isEmpty())
            throw new ApiException(ExceptionCode.NOT_FOUND, "예외처리");

        return result;
    }
}
