package com.example.user.unit.controller;

import com.example.common.exception.ApiErrorResponse;
import com.example.user.controller.ErrorHandler;
import com.example.user.exceptions.OtpInvalidException;
import com.example.user.exceptions.TokenRefreshException;
import com.example.user.exceptions.UserNotActiveException;
import com.example.user.exceptions.UserNotFoundException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ErrorHandlerTest {
    private static final int HTTP_UNAUTHORIZED = HttpStatus.UNAUTHORIZED.value();
    private static final int HTTP_NOT_FOUND = HttpStatus.NOT_FOUND.value();
    private static final int HTTP_FORBIDDEN = HttpStatus.FORBIDDEN.value();
    private static final int HTTP_BAD_REQUEST = HttpStatus.BAD_REQUEST.value();

    private static final String DEFAULT_AUTH_FAILED_MESSAGE = "Authentication failed";
    private static final String DEFAULT_USER_NOT_FOUND_MESSAGE = "User not found";
    private static final String DEFAULT_USER_NOT_ACTIVE_MESSAGE = "User account is not active";
    private static final String DEFAULT_INVALID_OTP_MESSAGE = "Invalid OTP code";

    private static final String CUSTOM_JWT_MESSAGE = "Invalid JWT token";
    private static final String CUSTOM_TOKEN_REFRESH_MESSAGE = "Token refresh failed";
    private static final String CUSTOM_USER_NOT_FOUND_MESSAGE = "User with id 123 not found";
    private static final String CUSTOM_USERNAME_NOT_FOUND_MESSAGE = "Username john.doe not found";
    private static final String CUSTOM_USER_NOT_ACTIVE_MESSAGE = "User account is suspended";
    private static final String CUSTOM_OTP_INVALID_MESSAGE = "The provided OTP code is invalid";

    @InjectMocks
    private ErrorHandler errorHandler;

    @ParameterizedTest
    @MethodSource("authenticationExceptionProvider")
    @DisplayName("Should handle authentication exceptions with custom message")
    void shouldHandleAuthenticationExceptionsWithCustomMessage(
            RuntimeException exception,
            String expectedMessage
    ) {
        // when
        ResponseEntity<ApiErrorResponse> response = errorHandler.handleAuthenticationExceptions(exception);

        // then
        assertErrorResponse(response, HttpStatus.UNAUTHORIZED, HTTP_UNAUTHORIZED, expectedMessage);
    }

    @ParameterizedTest
    @MethodSource("authenticationExceptionNullMessageProvider")
    @DisplayName("Should handle authentication exceptions with null message")
    void shouldHandleAuthenticationExceptionsWithNullMessage(RuntimeException exception) {
        // when
        ResponseEntity<ApiErrorResponse> response = errorHandler.handleAuthenticationExceptions(exception);

        // then
        assertErrorResponse(response, HttpStatus.UNAUTHORIZED, HTTP_UNAUTHORIZED, DEFAULT_AUTH_FAILED_MESSAGE);
    }

    @ParameterizedTest
    @MethodSource("userNotFoundExceptionProvider")
    @DisplayName("Should handle user not found exceptions with custom message")
    void shouldHandleUserNotFoundExceptionsWithCustomMessage(
            RuntimeException exception,
            String expectedMessage
    ) {
        // when
        ResponseEntity<ApiErrorResponse> response = errorHandler.handleUserNotFoundException(exception);

        // then
        assertErrorResponse(response, HttpStatus.NOT_FOUND, HTTP_NOT_FOUND, expectedMessage);
    }

    @ParameterizedTest
    @MethodSource("userNotFoundExceptionNullMessageProvider")
    @DisplayName("Should handle user not found exceptions with null message")
    void shouldHandleUserNotFoundExceptionsWithNullMessage(RuntimeException exception) {
        // when
        ResponseEntity<ApiErrorResponse> response = errorHandler.handleUserNotFoundException(exception);

        // then
        assertErrorResponse(response, HttpStatus.NOT_FOUND, HTTP_NOT_FOUND, DEFAULT_USER_NOT_FOUND_MESSAGE);
    }

    @ParameterizedTest
    @MethodSource("userNotActiveExceptionProvider")
    @DisplayName("Should handle user not active exception")
    void shouldHandleUserNotActiveException(
            UserNotActiveException exception,
            String expectedMessage
    ) {
        // when
        ResponseEntity<ApiErrorResponse> response = errorHandler.handleUserNotActiveException(exception);

        // then
        assertErrorResponse(response, HttpStatus.FORBIDDEN, HTTP_FORBIDDEN, expectedMessage);
    }

    @ParameterizedTest
    @MethodSource("otpInvalidExceptionProvider")
    @DisplayName("Should handle OTP invalid exception")
    void shouldHandleOtpInvalidException(
            OtpInvalidException exception,
            String expectedMessage
    ) {
        // when
        ResponseEntity<ApiErrorResponse> response = errorHandler.handleOtpInvalidException(exception);

        // then
        assertErrorResponse(response, HttpStatus.BAD_REQUEST, HTTP_BAD_REQUEST, expectedMessage);
    }

    private void assertErrorResponse(
            ResponseEntity<ApiErrorResponse> response,
            HttpStatus expectedStatus,
            int expectedStatusCode,
            String expectedMessage
    ) {
        assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(expectedStatusCode);
        assertThat(response.getBody().getMessage()).isEqualTo(expectedMessage);
    }

    private static Stream<Arguments> authenticationExceptionProvider() {
        return Stream.of(
                Arguments.of(new JwtException(CUSTOM_JWT_MESSAGE), CUSTOM_JWT_MESSAGE),
                Arguments.of(new TokenRefreshException(CUSTOM_TOKEN_REFRESH_MESSAGE), CUSTOM_TOKEN_REFRESH_MESSAGE)
        );
    }

    private static Stream<Arguments> authenticationExceptionNullMessageProvider() {
        return Stream.of(
                Arguments.of(new JwtException(null)),
                Arguments.of(new TokenRefreshException(null))
        );
    }

    private static Stream<Arguments> userNotFoundExceptionProvider() {
        return Stream.of(
                Arguments.of(new UserNotFoundException(CUSTOM_USER_NOT_FOUND_MESSAGE), CUSTOM_USER_NOT_FOUND_MESSAGE),
                Arguments.of(new UsernameNotFoundException(CUSTOM_USERNAME_NOT_FOUND_MESSAGE), CUSTOM_USERNAME_NOT_FOUND_MESSAGE)
        );
    }

    private static Stream<Arguments> userNotFoundExceptionNullMessageProvider() {
        return Stream.of(
                Arguments.of(new UserNotFoundException(null)),
                Arguments.of(new UsernameNotFoundException(null))
        );
    }

    private static Stream<Arguments> userNotActiveExceptionProvider() {
        return Stream.of(
                Arguments.of(new UserNotActiveException(CUSTOM_USER_NOT_ACTIVE_MESSAGE), CUSTOM_USER_NOT_ACTIVE_MESSAGE),
                Arguments.of(new UserNotActiveException(null), DEFAULT_USER_NOT_ACTIVE_MESSAGE)
        );
    }

    private static Stream<Arguments> otpInvalidExceptionProvider() {
        return Stream.of(
                Arguments.of(new OtpInvalidException(CUSTOM_OTP_INVALID_MESSAGE), CUSTOM_OTP_INVALID_MESSAGE),
                Arguments.of(new OtpInvalidException(null), DEFAULT_INVALID_OTP_MESSAGE)
        );
    }
}

