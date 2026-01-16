package com.example.user.unit.domain.dto.user.request;

import com.example.user.domain.dto.user.request.RegisterUserRequest;
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

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Set;

public class RegisterUserRequestTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("Should pass validation when all data is correct")
    void shouldPassValidation() {
        // given
        RegisterUserRequest request = validRequest().build();

        // when
        Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }


    // EMAIL VALIDATION
    @ParameterizedTest
    @ValueSource(strings = {"plain-string-no-at", "@missing-username.com", "username-at-missing-domain@", "user name@domain.com"})
    @NullAndEmptySource // Also checks null and empty strings
    @DisplayName("Should fail when email format is invalid or empty")
    void shouldFailWhenEmailIsInvalid(String invalidEmail) {
        // given
        RegisterUserRequest request = validRequest()
                .email(invalidEmail)
                .build();

        // when
        Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

        // then
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("email");
    }

    // PASSWORD VALIDATION
    @ParameterizedTest
    @ValueSource(strings = {"12345", "abcde", "short"})
    @DisplayName("Should fail when password is too short (< 6 chars)")
    void shouldFailWhenPasswordIsTooShort(String shortPassword) {
        // given
        RegisterUserRequest request = validRequest().password(shortPassword).build();

        // when
        Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

        // then
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("Password must be at least 6 characters long");
    }

    // PASSWORD COMPLEXITY VALIDATION
    @ParameterizedTest
    @ValueSource(strings = {"onlyletters", "123456789", "!!@@##$$"})
    @DisplayName("Should fail when password does not contain both letters and digits")
    void shouldFailWhenPasswordComplexityIsNotMet(String weakPassword) {
        // given
        RegisterUserRequest request = validRequest().password(weakPassword).build();

        // when
        Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

        // then
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("Password must contain at least one letter and one digit");
    }

    // REQUIRED FIELDS
    @Test
    @DisplayName("Should fail when required fields (OTP, ConfirmPassword) are missing")
    void shouldFailWhenRequiredFieldsAreMissing() {
        // given
        RegisterUserRequest request = validRequest()
                .otp(null)              // Missing OTP
                .confirmPassword("")    // Empty confirm password
                .build();

        // when
        Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(2);
        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .containsExactlyInAnyOrder("otp", "confirmPassword");
    }

    private RegisterUserRequest.RegisterUserRequestBuilder validRequest() {
        return RegisterUserRequest.builder()
                .email("correct@example.com")
                .password("StrongPass1")
                .confirmPassword("StrongPass1")
                .otp("123456");
    }
}
