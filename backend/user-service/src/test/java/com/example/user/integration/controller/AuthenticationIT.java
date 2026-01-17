package com.example.user.integration.controller;

import com.example.user.domain.dto.user.request.LoginUserRequest;
import com.example.user.domain.dto.user.request.RegisterUserRequest;
import com.example.user.domain.entities.Role;
import com.example.user.integration.BaseIT;
import com.example.user.repository.RoleRepository;
import com.example.user.repository.UserRepository;
import com.example.user.service.OtpCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthenticationIT extends BaseIT {

    private static final String AUTH_BASE_URL = "/api/v1/auth";
    private static final String REGISTER_URL = AUTH_BASE_URL + "/register";
    private static final String LOGIN_URL = AUTH_BASE_URL + "/login";
    private static final String REFRESH_URL = AUTH_BASE_URL + "/refresh";
    private static final String LOGOUT_URL = AUTH_BASE_URL + "/logout";

    private static final String TEST_EMAIL = "integration@test.com";
    private static final String TEST_PASSWORD = "TestPassword123!";
    private static final String TEST_OTP = "123456";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private OtpCacheService otpCacheService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        if (roleRepository.findByName("USER").isEmpty()) {
            Role userRole = Role.builder()
                    .name("USER")
                    .build();
            roleRepository.save(userRole);
        }
    }

    @Test
    @DisplayName("Should complete full authentication flow: register → login → refresh → logout")
    void shouldCompleteFullAuthenticationFlow() throws Exception {
        // given
        otpCacheService.saveOtp(TEST_EMAIL, TEST_OTP);

        RegisterUserRequest registerRequest = RegisterUserRequest.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .confirmPassword(TEST_PASSWORD)
                .otp(TEST_OTP)
                .build();

        MvcResult registerResult = mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.user.active").value(true))
                .andExpect(header().exists("Set-Cookie"))
                .andReturn();

        String registerCookie = registerResult.getResponse().getHeader("Set-Cookie");
        assertThat(registerCookie).contains("refreshToken");

        LoginUserRequest loginRequest = LoginUserRequest.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        // when
        MvcResult loginResult = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value(TEST_EMAIL))
                .andExpect(header().exists("Set-Cookie"))
                .andReturn();

        String loginCookie = loginResult.getResponse().getHeader("Set-Cookie");
        String refreshToken = extractRefreshToken(loginCookie);

        // then
        mockMvc.perform(post(REFRESH_URL)
                        .cookie(createCookie("refreshToken", refreshToken)))
                .andExpect(status().isOk())
                .andExpect(content().string(not(emptyString())));

        // ========== STEP 4: LOGOUT ==========
        mockMvc.perform(post(LOGOUT_URL)
                        .cookie(createCookie("refreshToken", refreshToken)))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", containsString("Max-Age=0")));

        // ========== STEP 5: VERIFY TOKEN INVALIDATED ==========
        mockMvc.perform(post(REFRESH_URL)
                        .cookie(createCookie("refreshToken", refreshToken)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should fail registration with existing email")
    void shouldFailRegistrationWithExistingEmail() throws Exception {
        // given - prepare OTP and first registration
        otpCacheService.saveOtp(TEST_EMAIL, TEST_OTP);

        RegisterUserRequest firstRequest = RegisterUserRequest.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .confirmPassword(TEST_PASSWORD)
                .otp(TEST_OTP)
                .build();

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isCreated());

        // when - duplicate registration with new OTP
        otpCacheService.saveOtp(TEST_EMAIL, TEST_OTP);

        RegisterUserRequest duplicateRequest = RegisterUserRequest.builder()
                .email(TEST_EMAIL)
                .password("DifferentPassword123!")
                .confirmPassword("DifferentPassword123!")
                .otp(TEST_OTP)
                .build();

        // then
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail login with invalid credentials")
    void shouldFailLoginWithInvalidCredentials() throws Exception {
        // given - prepare OTP and registered user
        otpCacheService.saveOtp(TEST_EMAIL, TEST_OTP);

        RegisterUserRequest registerRequest = RegisterUserRequest.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .confirmPassword(TEST_PASSWORD)
                .otp(TEST_OTP)
                .build();

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // when - login with wrong password
        LoginUserRequest loginRequest = LoginUserRequest.builder()
                .email(TEST_EMAIL)
                .password("WrongPassword123!")
                .build();

        // then
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should fail refresh with missing token")
    void shouldFailRefreshWithMissingToken() throws Exception {
        // when & then
        mockMvc.perform(post(REFRESH_URL))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Refresh token is missing")));
    }

    @Test
    @DisplayName("Should fail refresh with invalid token")
    void shouldFailRefreshWithInvalidToken() throws Exception {
        // when & then
        mockMvc.perform(post(REFRESH_URL)
                        .cookie(createCookie("refreshToken", "invalid.token.here")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should validate registration request fields")
    void shouldValidateRegistrationRequestFields() throws Exception {
        // given - invalid email (validation should fail before OTP check)
        RegisterUserRequest invalidRequest = RegisterUserRequest.builder()
                .email("invalid-email")
                .password(TEST_PASSWORD)
                .confirmPassword(TEST_PASSWORD)
                .otp(TEST_OTP)
                .build();

        // when & then
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should validate login request fields")
    void shouldValidateLoginRequestFields() throws Exception {
        // given - empty password
        LoginUserRequest invalidRequest = LoginUserRequest.builder()
                .email(TEST_EMAIL)
                .password("")
                .build();

        // when & then
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    private String extractRefreshToken(String setCookieHeader) {
        if (setCookieHeader == null) {
            return null;
        }
        String[] parts = setCookieHeader.split(";");
        for (String part : parts) {
            if (part.trim().startsWith("refreshToken=")) {
                return part.trim().substring("refreshToken=".length());
            }
        }
        return null;
    }

    private jakarta.servlet.http.Cookie createCookie(String name, String value) {
        return new jakarta.servlet.http.Cookie(name, value);
    }
}

