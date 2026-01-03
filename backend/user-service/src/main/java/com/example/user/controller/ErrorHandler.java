package com.example.user.controller;

import com.example.common.exception.ApiErrorResponse;
import com.example.common.exception.GlobalErrorHandler;
import com.example.user.exceptions.OtpInvalidException;
import com.example.user.exceptions.TokenRefreshException;
import com.example.user.exceptions.UserNotActiveException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class ErrorHandler extends GlobalErrorHandler {
    @ExceptionHandler({
            JwtException.class,
            UsernameNotFoundException.class,
            TokenRefreshException.class
    })
    public ResponseEntity<ApiErrorResponse> handleAuthenticationExceptions(RuntimeException e) {
        // Authentication related exceptions - invalid tokens, user not found, token refresh issues
        log.error("Authentication exception: {} - {}", e.getClass().getSimpleName(), e.getMessage(), e);

        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message(e.getMessage() != null ? e.getMessage() : "Authentication failed")
                .build();
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserNotActiveException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotActiveException(UserNotActiveException e) {
        // User exists but is not active - authorization issue
        log.error("User not active exception: {}", e.getMessage(), e);

        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message(e.getMessage() != null ? e.getMessage() : "User account is not active")
                .build();
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(OtpInvalidException.class)
    public ResponseEntity<ApiErrorResponse> handleOtpInvalidException(OtpInvalidException e) {
        // Invalid OTP code provided
        log.error("OTP validation exception: {}", e.getMessage(), e);

        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage() != null ? e.getMessage() : "Invalid OTP code")
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
