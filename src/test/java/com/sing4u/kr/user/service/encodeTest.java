package com.sing4u.kr.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public class encodeTest {

    @Test
    void encodeTest() {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        String rawPassword = "user_1";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        System.out.println(encodedPassword);  // 이미 prefix 포함됨: {bcrypt}...
    }
}
