package com.sing4u.kr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


import java.util.TimeZone;
@EnableCaching
@EnableJpaAuditing
@SpringBootApplication
@EntityScan(basePackages = "com.sing4u.kr")
public class Sing4UApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
		SpringApplication.run(Sing4UApplication.class, args);
	}

}
