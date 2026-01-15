package com.example.user.service.impl;

import com.example.user.service.EmailService;
import com.example.user.service.OtpCacheService;
import com.example.user.utility.OtpCodeGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OtpServiceImplTest {
    @Mock private OtpCodeGenerator otpCodeGenerator;
    @Mock private OtpCacheService otpCacheService;
    @Mock private EmailService emailService;

    @InjectMocks
    private OtpServiceImpl otpService;

    private final String EMAIL = "john.doe@example.com";
    private final String OTP = "123456";

    @Test
    @DisplayName("Should process OTP request successfully")
    void shouldProcessOtpRequestSuccessfully() {
        // given
        when(otpCodeGenerator.generateOtp(6)).thenReturn(OTP);

        // when
        otpService.processOtpRequest(EMAIL);

        // then
        verify(otpCodeGenerator).generateOtp(6);
        verify(otpCacheService).saveOtp(EMAIL, OTP);
        verify(emailService).sendRegistrationEmail(EMAIL, OTP);
    }

    @Test
    @DisplayName("Should generate OTP successfully")
    void shouldGenerateOtpSuccessfully() {
        // given
        when(otpCodeGenerator.generateOtp(6)).thenReturn(OTP);

        // when
        String result = otpService.generateOtp(EMAIL);

        // then
        assertThat(result).isEqualTo(OTP);
        verify(otpCodeGenerator).generateOtp(6);
    }

    @Test
    @DisplayName("Should verify OTP successfully when OTP is correct")
    void shouldVerifyOtpSuccessfullyWhenOtpIsCorrect() {
        // given
        when(otpCacheService.getOtp(EMAIL)).thenReturn(OTP);

        // when
        boolean result = otpService.verifyOtp(EMAIL, OTP);

        // then
        assertThat(result).isTrue();
        verify(otpCacheService).getOtp(EMAIL);
        verify(otpCacheService).deleteOtp(EMAIL);
    }

    @Test
    @DisplayName("Should return false when saved OTP is null")
    void shouldReturnFalseWhenSavedOtpIsNull() {
        // given
        when(otpCacheService.getOtp(EMAIL)).thenReturn(null);

        // when
        boolean result = otpService.verifyOtp(EMAIL, OTP);

        // then
        assertThat(result).isFalse();
        verify(otpCacheService).getOtp(EMAIL);
        verify(otpCacheService, never()).deleteOtp(EMAIL);
    }

    @Test
    @DisplayName("Should return false when OTP does not match")
    void shouldReturnFalseWhenOtpDoesNotMatch() {
        // given
        when(otpCacheService.getOtp(EMAIL)).thenReturn("654321");

        // when
        boolean result = otpService.verifyOtp(EMAIL, OTP);

        // then
        assertThat(result).isFalse();
        verify(otpCacheService).getOtp(EMAIL);
        verify(otpCacheService, never()).deleteOtp(EMAIL);
    }

    @Test
    @DisplayName("Should return false when provided OTP is null")
    void shouldReturnFalseWhenProvidedOtpIsNull() {
        // given
        when(otpCacheService.getOtp(EMAIL)).thenReturn(OTP);

        // when
        boolean result = otpService.verifyOtp(EMAIL, null);

        // then
        assertThat(result).isFalse();
        verify(otpCacheService).getOtp(EMAIL);
        verify(otpCacheService, never()).deleteOtp(EMAIL);
    }

    @Test
    @DisplayName("Should return false when provided OTP is empty")
    void shouldReturnFalseWhenProvidedOtpIsEmpty() {
        // given
        when(otpCacheService.getOtp(EMAIL)).thenReturn(OTP);

        // when
        boolean result = otpService.verifyOtp(EMAIL, "");

        // then
        assertThat(result).isFalse();
        verify(otpCacheService).getOtp(EMAIL);
        verify(otpCacheService, never()).deleteOtp(EMAIL);
    }

    @Test
    @DisplayName("Should handle email service call in processOtpRequest")
    void shouldHandleEmailServiceCallInProcessOtpRequest() {
        // given
        when(otpCodeGenerator.generateOtp(6)).thenReturn(OTP);
        doNothing().when(emailService).sendRegistrationEmail(EMAIL, OTP);

        // when
        otpService.processOtpRequest(EMAIL);

        // then
        verify(emailService).sendRegistrationEmail(EMAIL, OTP);
    }

    @Test
    @DisplayName("Should call cache service before email service in processOtpRequest")
    void shouldCallCacheServiceBeforeEmailServiceInProcessOtpRequest() {
        // given
        when(otpCodeGenerator.generateOtp(6)).thenReturn(OTP);

        // when
        otpService.processOtpRequest(EMAIL);

        // then
        var inOrder = inOrder(otpCacheService, emailService);
        inOrder.verify(otpCacheService).saveOtp(EMAIL, OTP);
        inOrder.verify(emailService).sendRegistrationEmail(EMAIL, OTP);
    }
}

