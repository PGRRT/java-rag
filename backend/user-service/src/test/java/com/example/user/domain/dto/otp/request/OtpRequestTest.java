package com.example.user.domain.dto.otp.request;

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

class OtpRequestTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("Should pass validation when email is correct")
    void shouldPassValidation() {
        // given
        OtpRequest request = validRequest().build();

        // when
        Set<ConstraintViolation<OtpRequest>> violations = validator.validate(request);

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
        OtpRequest request = validRequest()
                .email(invalidEmail)
                .build();

        // when
        Set<ConstraintViolation<OtpRequest>> violations = validator.validate(request);

        // then
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("email");
    }

    // HELPERS
    private OtpRequest.OtpRequestBuilder validRequest() {
        return OtpRequest.builder()
                .email("test@example.com");
    }
}