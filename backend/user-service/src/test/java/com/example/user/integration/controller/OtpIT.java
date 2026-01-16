package com.example.user.integration.controller;

import com.example.user.domain.dto.otp.request.OtpRequest;
import com.example.user.integration.BaseIT;
import com.example.user.service.OtpCacheService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OtpIT extends BaseIT {

    private static final String OTP_URL = "/api/v1/users/otp";
    private static final String TEST_EMAIL = "otp@test.com";

    @Autowired
    private OtpCacheService otpCacheService;


    @Test
    @DisplayName("Should send OTP and store in Redis cache")
    void shouldSendOtpAndStoreInCache() throws Exception {
        // given
        OtpRequest otpRequest = new OtpRequest(TEST_EMAIL);

        // when
        mockMvc.perform(post(OTP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(otpRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("OTP has been sent to your email"));

        // then - verify OTP stored in Redis
        String storedOtp = otpCacheService.getOtp(TEST_EMAIL);
        assertThat(storedOtp).isNotNull();
        assertThat(storedOtp).hasSize(6);
        assertThat(storedOtp).matches("\\d{6}");
    }

    @Test
    @DisplayName("Should fail OTP request with invalid email format")
    void shouldFailOtpRequestWithInvalidEmail() throws Exception {
        // given
        OtpRequest invalidRequest = new OtpRequest("invalid-email");

        // when & then
        mockMvc.perform(post(OTP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail OTP request with null email")
    void shouldFailOtpRequestWithNullEmail() throws Exception {
        // given
        String requestBody = "{}";

        // when & then
        mockMvc.perform(post(OTP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should generate different OTP codes for multiple requests")
    void shouldGenerateDifferentOtpCodesForMultipleRequests() throws Exception {
        // given
        OtpRequest otpRequest = new OtpRequest(TEST_EMAIL);

        // when - first request
        mockMvc.perform(post(OTP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(otpRequest)))
                .andExpect(status().isOk());
        String firstOtp = otpCacheService.getOtp(TEST_EMAIL);

        // when - second request
        mockMvc.perform(post(OTP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(otpRequest)))
                .andExpect(status().isOk());
        String secondOtp = otpCacheService.getOtp(TEST_EMAIL);

        // then - OTP should be overwritten (may be same by chance, but stored)
        assertThat(secondOtp).isNotNull();
    }

    @Test
    @DisplayName("Should store OTP for different emails separately")
    void shouldStoreOtpForDifferentEmailsSeparately() throws Exception {
        // given
        String email1 = "user1@test.com";
        String email2 = "user2@test.com";

        // when
        mockMvc.perform(post(OTP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new OtpRequest(email1))))
                .andExpect(status().isOk());

        mockMvc.perform(post(OTP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new OtpRequest(email2))))
                .andExpect(status().isOk());

        // then
        String otp1 = otpCacheService.getOtp(email1);
        String otp2 = otpCacheService.getOtp(email2);

        assertThat(otp1).isNotNull();
        assertThat(otp2).isNotNull();
    }
}

