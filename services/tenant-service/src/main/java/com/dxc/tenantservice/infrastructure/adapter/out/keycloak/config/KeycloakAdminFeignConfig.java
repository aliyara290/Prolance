package com.dxc.tenantservice.infrastructure.adapter.out.keycloak.config;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@Slf4j
public class KeycloakAdminFeignConfig {

    @Bean
    public RequestInterceptor keycloakAdminRequestInterceptor(ApplicationContext context) {
        return template -> {
            KeycloakTokenProvider tokenProvider = context.getBean(KeycloakTokenProvider.class);
            String token = tokenProvider.getValidToken();
            template.header("Authorization", "Bearer " + token);
        };
    }
}
