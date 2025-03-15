package com.sing4u.kr.hello.application.command;

import com.sing4u.kr.hello.domain.Hello;
import com.sing4u.kr.hello.domain.HelloRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateHelloUseCase {
    private final HelloRepository helloRepository;

    @Transactional
    public Hello execute(String message) {
        Hello hello = new Hello(message);

        return helloRepository.save(hello);
    }
}