package com.example.gateway.config;

import com.example.gateway.filter.RateLimitFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class GatewayConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, RateLimitFilter rateLimitFilter) {
        return builder.routes()
                .route("user-service", r -> r
                        .path("/api/v1/users/**", "/api/v1/auth/**", "/api/v1/user/**")
                        .filters(f -> f.filter(rateLimitFilter))
                        .uri("lb://user-service"))
                .route("chat-service", r -> r
                        .path("/api/v1/chats/**")
                        .filters(f -> f.filter(rateLimitFilter))
                        .uri("lb://chat-service"))
                .build();
    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(10, 20, 1); // 5 requests per second with a burst capacity of 10
    }

    @Bean
    public KeyResolver keyResolver() {
        return exchange -> {
            String ipAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            return Mono.just(ipAddress);
        };
    }
}
