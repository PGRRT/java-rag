package com.example.user.controller;

import com.example.common.exception.ApiErrorResponse;
import com.example.common.exception.GlobalErrorHandler;
import com.example.user.exceptions.*;
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
    @ExceptionHandler(EmailAlreadyTakenException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailAlreadyTakenException(EmailAlreadyTakenException e) {
        log.warn("Registration conflict: {}", e.getMessage());

        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value()) // 409
                .message(e.getMessage() != null ? e.getMessage() : "Email is already in use")
                .build();

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({
            JwtException.class,
            TokenRefreshException.class,
            InvalidTokenException.class
    })
    public ResponseEntity<ApiErrorResponse> handleAuthenticationExceptions(RuntimeException e) {
        // Authentication related exceptions - invalid tokens, user not found, token refresh issues

        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message(e.getMessage() != null ? e.getMessage() : "Authentication failed")
                .build();
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            UsernameNotFoundException.class,
    })
    public ResponseEntity<ApiErrorResponse> handleUserNotFoundException(RuntimeException e) {
        // User not found exceptions
        log.warn("User not found exception: {}", e.getMessage(), e);

        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(e.getMessage() != null ? e.getMessage() : "User not found")
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotActiveException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotActiveException(UserNotActiveException e) {
        // User exists but is not active - authorization issue
        log.warn("User not active exception: {}", e.getMessage(), e);

        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message(e.getMessage() != null ? e.getMessage() : "User account is not active")
                .build();
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(OtpInvalidException.class)
    public ResponseEntity<ApiErrorResponse> handleOtpInvalidException(OtpInvalidException e) {
        // Invalid OTP code provided
        log.warn("OTP validation exception: {}", e.getMessage(), e);

        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage() != null ? e.getMessage() : "Invalid OTP code")
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
