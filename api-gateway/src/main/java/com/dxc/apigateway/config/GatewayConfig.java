package com.dxc.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {

        return builder.routes()

                .route("tenant-service", r -> r
                        .path("/tenant/**")
                        .filters(f -> f
                                .rewritePath("/tenant/(?<segment>.*)", "/${segment}")
                        )
                        .uri("lb://TENANT-SERVICE"))
                .route("crm-service", r -> r
                        .path("/crm/**")
                        .filters(f -> f
                                .rewritePath("/crm/(?<segment>.*)", "/${segment}")
                        )
                        .uri("lb://CRM-SERVICE"))
                .build();
    }
}