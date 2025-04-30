package com.sing4u.kr.health.controller;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.sing4u.kr.health.dto.SwaggerAuthResponse;
import com.sing4u.kr.health.service.SwaggerService;

@RestController
@RequestMapping("/api/v1/swagger")
@RequiredArgsConstructor
public class SwaggerController {

    private final SwaggerService swaggerService;

    @Hidden
    @PostMapping(value = "/auth", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public SwaggerAuthResponse authForSwagger(@RequestParam(value = "grant_type") String grantType,
                                              @RequestParam String username,
                                              @RequestParam String password) {
        return swaggerService.swaggerAuthorization(username, password);
    }
}
