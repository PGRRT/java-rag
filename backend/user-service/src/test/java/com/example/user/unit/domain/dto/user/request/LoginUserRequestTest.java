package com.example.user.unit.domain.dto.user.request;

import com.example.user.domain.dto.user.request.LoginUserRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LoginUserRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("Should pass validation when email and password are correct")
    void shouldPassValidation() {
        // given
        LoginUserRequest request = validRequest().build();

        // when
        Set<ConstraintViolation<LoginUserRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    // EMAIL VALIDATION (@NotBlank, @Email)
    @ParameterizedTest
    @ValueSource(strings = {"plain-string", "user@", "@domain.com", "user name@domain.com"})
    @NullAndEmptySource
    @DisplayName("Should fail when email format is invalid, null, or empty")
    void shouldFailWhenEmailIsInvalid(String invalidEmail) {
        // given
        LoginUserRequest request = validRequest()
                .email(invalidEmail)
                .build();

        // when
        Set<ConstraintViolation<LoginUserRequest>> violations = validator.validate(request);

        // then
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("email");
    }

    // PASSWORD VALIDATION (@NotBlank, @Size, @Pattern)
    @Test
    @DisplayName("Should fail when password is null or blank")
    void shouldFailWhenPasswordIsMissing() {
        // given
        LoginUserRequest request = validRequest()
                .password(null) // or ""
                .build();

        // when
        Set<ConstraintViolation<LoginUserRequest>> violations = validator.validate(request);

        // then
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("password");
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345", "abcde", "short"})
    @DisplayName("Should fail when password is too short (< 6 chars)")
    void shouldFailWhenPasswordIsTooShort(String shortPassword) {
        // given
        LoginUserRequest request = validRequest()
                .password(shortPassword)
                .build();

        // when
        Set<ConstraintViolation<LoginUserRequest>> violations = validator.validate(request);

        // then
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("Password must be at least 6 characters long");
    }

    @ParameterizedTest
    @ValueSource(strings = {"onlyletters", "123456789", "!!@@##$$"})
    @DisplayName("Should fail when password does not contain both letters and digits")
    void shouldFailWhenPasswordComplexityIsNotMet(String weakPassword) {
        // given
        LoginUserRequest request = validRequest()
                .password(weakPassword)
                .build();

        // when
        Set<ConstraintViolation<LoginUserRequest>> violations = validator.validate(request);

        // then
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("Password must contain at least one letter and one digit");
    }

    // HELPERS
    private LoginUserRequest.LoginUserRequestBuilder validRequest() {
        return LoginUserRequest.builder()
                .email("test@example.com")
                .password("Password123"); // Meets requirements: >6 chars, letter, digit
    }
}