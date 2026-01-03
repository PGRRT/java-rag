package com.example.common.exception;

import com.example.common.exception.exceptions.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;

import java.util.List;

@Slf4j
public abstract class GlobalErrorHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception e) {
        log.error("An error occurred: {}", e.getMessage(), e);
        ApiErrorResponse error = ApiErrorResponse.builder()
                .message("An unexpected error occurred. Please try again later.")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("An error occurred: {}", e.getMessage(), e);
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalStateException(IllegalStateException e) {
        log.warn("An error occurred: {}", e.getMessage(), e);
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleCredentialsException(BadCredentialsException e) {
        log.warn("An error occurred: {}", e.getMessage(), e);
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message("Invalid credentials. Please check your email and password.")
                .build();
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("An error occurred: {}", e.getMessage(), e);
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .message("Request method not supported.")
                .build();
        return new ResponseEntity<>(error, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("An error occurred: {}", e.getMessage(), e);
        List<FieldError> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> FieldError.builder().field(fieldError.getField()).message(fieldError.getDefaultMessage()).build()
                )
                .toList();

        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed for one or more fields.")
                .fieldErrors(errors)
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Handle SSE client disconnects gracefully
    @ExceptionHandler(AsyncRequestNotUsableException.class)
    public ResponseEntity<?> handleAsyncRequestNotUsable(Exception e) {
        log.debug("SSE client disconnected: {}", e.toString());
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        log.warn("An error occurred: {}", e.getMessage(), e);

        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
