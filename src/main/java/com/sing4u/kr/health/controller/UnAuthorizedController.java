package com.sing4u.kr.health.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class UnAuthorizedController {

    @GetMapping("")
    public String unAuthTest() {
        return "unAuthTest";
    }
}
