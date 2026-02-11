package com.example.gateway.config;

import com.example.gateway.filter.RateLimitFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.factory.RetryGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

@Component
public class GatewayConfig {
    private static final HttpMethod[] IDEMPOTENT_METHODS = {
            HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.HEAD, HttpMethod.OPTIONS
    };

    private Consumer<RetryGatewayFilterFactory.RetryConfig> defaultRetryConfig() {
        return config -> config
                .setRetries(3)
                .setMethods(IDEMPOTENT_METHODS)
                .setSeries(HttpStatus.Series.SERVER_ERROR)
                .setExceptions(
                        IOException.class,
                        TimeoutException.class,
                        NotFoundException.class
                )
                .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, RateLimitFilter rateLimitFilter) {
        return builder.routes()
                .route("user-service", r -> r
                        .path("/api/v1/users/**", "/api/v1/auth/**", "/api/v1/user/**")
                        .filters(f -> f.filter(rateLimitFilter)
                                .retry(defaultRetryConfig()))
                        .uri("lb://user-service"))
                .route("chat-service", r -> r
                        .path("/api/v1/chats/**")
                        .filters(f -> f.filter(rateLimitFilter)
                                .retry(defaultRetryConfig())
                        )
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
