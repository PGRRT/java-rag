package com.example.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class GatewayConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r
                        .path("/api/v1/users/**", "/api/v1/auth/**", "/api/v1/user/**")
                        .uri("lb://user-service"))
                .route("chat-service", r -> r
                        .path("/api/v1/chats/**")
                        .uri("lb://chat-service"))
                .build();
    }
}
