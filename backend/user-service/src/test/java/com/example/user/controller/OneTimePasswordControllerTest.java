package com.example.user.controller;

import com.example.user.domain.dto.otp.request.OtpRequest;
import com.example.user.service.OtpService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OneTimePasswordController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class OneTimePasswordControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean private OtpService otpService;

    @Test
    @DisplayName("POST /otp - Should return 200 OK when email is valid")
    void shouldSendOtp_WhenRequestIsValid() throws Exception {
        // given
        OtpRequest request = new OtpRequest("john.doe@example.com");

        // when & then
        mockMvc.perform(post("/api/v1/users/otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("OTP has been sent to your email"));

        then(otpService).should(times(1)).processOtpRequest("john.doe@example.com");
    }

    @Test
    @DisplayName("POST /otp - Should return 400 Bad Request when email is invalid")
    void shouldReturn400_WhenEmailIsInvalid() throws Exception {
        // given
        OtpRequest request = new OtpRequest("bad-email");

        // when & then
        mockMvc.perform(post("/api/v1/users/otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        then(otpService).shouldHaveNoInteractions();
    }
}