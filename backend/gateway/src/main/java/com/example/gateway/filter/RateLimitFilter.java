package com.example.gateway.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class RateLimitFilter implements GatewayFilter {
    private final RedisRateLimiter rateLimiter;
    private final KeyResolver keyResolver;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return keyResolver.resolve(exchange)
                .defaultIfEmpty("anonymous")
                .flatMap(key -> {
                    Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);

                    String routeId = (route != null) ? route.getId() : "unknown_route";

                    return rateLimiter.isAllowed(routeId, key).flatMap(response -> {
                        if (response.isAllowed()) {
                            return chain.filter(exchange);
                        }

                        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

                        DataBuffer buffer = getTooManyRequestsResponse(exchange);

                        return exchange.getResponse().writeWith(Mono.just(buffer));
                    });
                });
    }

    private DataBuffer getTooManyRequestsResponse(ServerWebExchange exchange) {
        String body = """
            {
              "status": 429,
              "error": "Too Many Requests",
              "message": "Rate limit exceeded. Please try again later.",
              "retryAfter": "1"
            }
            """;

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().bufferFactory().wrap(bytes);
    }
}
