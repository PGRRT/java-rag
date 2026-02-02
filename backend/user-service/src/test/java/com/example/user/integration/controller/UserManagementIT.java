package com.example.user.integration.controller;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserManagementIT extends BaseIT {

    private static final String AUTH_BASE_URL = "/api/v1/auth";
    private static final String USERS_BASE_URL = "/api/v1/users";
    private static final String REGISTER_URL = AUTH_BASE_URL + "/register";
    private static final String ME_URL = USERS_BASE_URL + "/me";

    private static final String TEST_EMAIL = "user@test.com";
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
    @DisplayName("Should get current user details with valid JWT")
    void shouldGetCurrentUserWithValidJwt() throws Exception {
        // given - registered user
        String accessToken = registerUserAndGetToken();

        // when & then
        mockMvc.perform(get(ME_URL)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("Should fail to get current user without JWT")
    void shouldFailToGetCurrentUserWithoutJwt() throws Exception {
        // when & then
        mockMvc.perform(get(ME_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should fail to get current user with invalid JWT")
    void shouldFailToGetCurrentUserWithInvalidJwt() throws Exception {
        // when & then
        mockMvc.perform(get(ME_URL)
                        .header("Authorization", "Bearer invalid.jwt.token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should delete current user account")
    void shouldDeleteCurrentUserAccount() throws Exception {
        // given
        String accessToken = registerUserAndGetToken();
        long userCountBefore = userRepository.count();

        // when
        mockMvc.perform(delete(ME_URL)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());

        // then
        long userCountAfter = userRepository.count();
        assertThat(userCountAfter).isLessThan(userCountBefore);
        assertThat(userRepository.findByEmail(TEST_EMAIL)).isEmpty();
    }

    @Test
    @DisplayName("Should fail to delete user without JWT")
    void shouldFailToDeleteUserWithoutJwt() throws Exception {
        // when & then
        mockMvc.perform(delete(ME_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should not access deleted user account")
    void shouldNotAccessDeletedUserAccount() throws Exception {
        // given
        String accessToken = registerUserAndGetToken();

        // when - delete account
        mockMvc.perform(delete(ME_URL)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());

        // then - try to access with same token
        mockMvc.perform(get(ME_URL)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should have correct user role after registration")
    void shouldHaveCorrectUserRoleAfterRegistration() throws Exception {
        // given
        String accessToken = registerUserAndGetToken();

        // when & then
        mockMvc.perform(get(ME_URL)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("Should verify user exists in database after registration")
    void shouldVerifyUserExistsInDatabaseAfterRegistration() throws Exception {
        // when
        registerUserAndGetToken();

        // then
        assertThat(userRepository.findByEmail(TEST_EMAIL)).isPresent();
        assertThat(userRepository.count()).isEqualTo(1L);
    }


    @Test
    @DisplayName("Should have active status by default after registration")
    void shouldHaveActiveStatusByDefaultAfterRegistration() throws Exception {
        // when
        registerUserAndGetToken();

        // then
        var user = userRepository.findByEmail(TEST_EMAIL);
        assertThat(user).isPresent();
        assertThat(user.get().isActive()).isTrue();
    }

    private String registerUserAndGetToken() throws Exception {
        otpCacheService.saveOtp(TEST_EMAIL, TEST_OTP);

        RegisterUserRequest registerRequest = RegisterUserRequest.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .confirmPassword(TEST_PASSWORD)
                .otp(TEST_OTP)
                .build();

        MvcResult result = mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("accessToken").asText();
    }
}

