package com.example.user.integration.security;

import com.example.user.domain.dto.user.request.RegisterUserRequest;
import com.example.user.domain.entities.Role;
import com.example.user.integration.BaseIT;
import com.example.user.repository.RoleRepository;
import com.example.user.repository.UserRepository;
import com.example.user.service.OtpCacheService;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityIT extends BaseIT {

    private static final String AUTH_BASE_URL = "/api/v1/auth";
    private static final String USERS_BASE_URL = "/api/v1/users";
    private static final String REGISTER_URL = AUTH_BASE_URL + "/register";
    private static final String ME_URL = USERS_BASE_URL + "/me";

    private static final String TEST_EMAIL = "user@test.com";
    private static final String TEST_PASSWORD = "TestPassword123!";
    private static final String TEST_OTP = "123456";

    private static final String ACCESS_TOKEN_EXPIRED = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiNDZlYmQxMC1lOTE2LTRjNWMtYmJmNS01YTA2ZmIxZmVjYWIiLCJlbWFpbCI6ImJrdWJhMTQwMUBnbWFpbC5jb20iLCJyb2xlIjoiVVNFUiIsInR5cGUiOiJhY2Nlc3MiLCJpYXQiOjE3Njg1OTE2OTEsImV4cCI6MTc2ODU5MjU5MSwiaXNzIjoic2lnbmFyby5jb20iLCJqdGkiOiIyMzAxMmZjMy1kZDgzLTQ5OGYtYWVmYS01NTkyNGUzMWNmZjUifQ.7SCqMnRU9a8AhpseM32sPn_Omx0QHYG2UJPr8tdhYTIROK9-1hboHbFpfWFmRhx9hpxH3x01AvTq2nCJyKR-Og";
    private static final String REFRESH_TOKEN_EXPIRED = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiNDZlYmQxMC1lOTE2LTRjNWMtYmJmNS01YTA2ZmIxZmVjYWIiLCJlbWFpbCI6ImJrdWJhMTQwMUBnbWFpbC5jb20iLCJyb2xlIjoiVVNFUiIsInR5cGUiOiJyZWZyZXNoIiwiaWF0IjoxNzY4NTkxNTg2LCJleHAiOjE3NjkxOTYzODYsImlzcyI6InNpZ25hcm8uY29tIiwianRpIjoiODZkYjAwYjQtYmRmMC00NDI0LTgyMTMtMDU3NWQ2NjNmNDdhIn0.zxkvHhWsYtdL6dxt3oWNkQsTFtDYYf_sHgDSOr9bA6H5JaJFAhHhQmaHCGvmvcVULNerm231-usuA3a7GI6kDg";

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
    @DisplayName("Should deny access to protected endpoints without authentication")
    void shouldDenyAccessToProtectedEndpointsWithoutAuth() throws Exception {
        // when & then
        mockMvc.perform(get(ME_URL))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(delete(ME_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should deny access with expired JWT token")
    void shouldDenyAccessWithExpiredToken() throws Exception {

        // when & then
        mockMvc.perform(get(ME_URL)
                        .header("Authorization", "Bearer " + ACCESS_TOKEN_EXPIRED))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should deny refresh token")
    void shouldDenyRefreshToken() throws Exception {

        // when & then
        mockMvc.perform(get(ME_URL)
                        .header("Authorization", "Bearer " + REFRESH_TOKEN_EXPIRED))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should deny access with malformed JWT token")
    void shouldDenyAccessWithMalformedToken() throws Exception {
        // given
        String malformedToken = "this-is-not-a-valid-jwt-token";

        // when & then
        mockMvc.perform(get(ME_URL)
                        .header("Authorization", "Bearer " + malformedToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should deny access with missing Bearer prefix")
    void shouldDenyAccessWithMissingBearerPrefix() throws Exception {
        // given
        String accessToken = registerUserAndGetToken();

        // when & then - without "Bearer " prefix
        mockMvc.perform(get(ME_URL)
                        .header("Authorization", accessToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should allow authenticated user to access their own resources")
    void shouldAllowAuthenticatedUserToAccessOwnResources() throws Exception {
        // given
        String accessToken = registerUserAndGetToken();

        // when & then
        mockMvc.perform(get(ME_URL)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should validate password strength during registration")
    void shouldValidatePasswordStrengthDuringRegistration() throws Exception {
        // given - weak password
        RegisterUserRequest weakPasswordRequest = createRegisterRequest("weak@test.com", "weak", "weak");

        // when & then
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(weakPasswordRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should prevent SQL injection in email parameter")
    void shouldPreventSqlInjectionInEmailParameter() throws Exception {
        // given - SQL injection attempt
        RegisterUserRequest sqlInjectionRequest = createRegisterRequest(
                "test@test.com' OR '1'='1",
                "TestPassword123!",
                "TestPassword123!"
        );

        // when & then - should fail validation
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sqlInjectionRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should prevent XSS in user input")
    void shouldPreventXssInUserInput() throws Exception {
        // given - XSS attempt
        RegisterUserRequest xssRequest = createRegisterRequest(
                "<script>alert('xss')</script>@test.com",
                "TestPassword123!",
                "TestPassword123!"
        );

        // when & then - should fail validation
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(xssRequest)))
                .andExpect(status().isBadRequest());
    }

    private String registerUserAndGetToken() throws Exception {

        RegisterUserRequest registerRequest = createRegisterRequest(
                TEST_EMAIL,
                TEST_PASSWORD,
                TEST_PASSWORD
        );

        MvcResult result = mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("accessToken").asText();
    }

    private RegisterUserRequest createRegisterRequest(String email, String password, String confirmPassword) {
        otpCacheService.saveOtp(TEST_EMAIL, TEST_OTP);

        return RegisterUserRequest.builder()
                .email(email)
                .password(password)
                .confirmPassword(confirmPassword)
                .otp(TEST_OTP)
                .build();
    }
}

