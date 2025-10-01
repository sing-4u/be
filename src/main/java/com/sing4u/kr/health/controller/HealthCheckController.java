package com.sing4u.kr.health.controller;


import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
@Hidden
public class HealthCheckController {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

}
