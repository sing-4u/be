package com.sing4u.kr.application.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;


@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    @Value("${sing4u.server-url}")
    private String serverUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Sing4U API").description("Sing4U API").version("v1"))
                .addServersItem(new Server().url(this.serverUrl))
                .addSecurityItem(new SecurityRequirement().addList("OAUTH2"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("OAUTH2", new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .flows(new OAuthFlows()
                                        .password(new OAuthFlow()
                                                .tokenUrl(serverUrl + "/api/v1/swagger/auth")
                                                .scopes(new Scopes()
                                                        .addString("read", "read all")
                                                        .addString("write", "write all")
                                                )
                                        )
                                )
                        )
                );
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("v1")
                .pathsToMatch("/api/**")
                .addOpenApiCustomizer(openApi -> openApi.info(new Info().title("Sing4U API").description("Sing4U API")))
                .build();
    }

    @Bean
    public MessageSource translator() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("swagger-message");
        source.setUseCodeAsDefaultMessage(true);
        source.setDefaultEncoding("utf-8");
        return source;
    }
}
