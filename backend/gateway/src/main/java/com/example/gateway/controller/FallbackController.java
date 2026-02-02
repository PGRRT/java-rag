package com.example.gateway.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class FallbackController {

    @GetMapping("/fallback/rate-limit")
    public ResponseEntity<Map<String, Object>> rateLimitFallback() {

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .header("Retry-After", "1")
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 429,
                        "error", "Too Many Requests",
                        "message", "Rate limit exceeded. Please try again later."
                ));
    }
}
