package com.example.user.unit.service.impl;

import com.example.user.service.impl.OtpCacheServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpCacheServiceImplTest {
    @Mock private StringRedisTemplate redisTemplate;
    @Mock private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private OtpCacheServiceImpl otpCacheService;

    private static final String EMAIL = "john.doe@example.com";
    private static final String OTP = "123456";
    private static final String KEY_PREFIX = "otp:";
    private static final Duration OTP_TTL = Duration.ofMinutes(5);

    @Test
    @DisplayName("Should save OTP to Redis with correct TTL")
    void shouldSaveOtpToRedisWithCorrectTtl() {
        // given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // when
        otpCacheService.saveOtp(EMAIL, OTP);

        // then
        verify(redisTemplate).opsForValue();
        verify(valueOperations).set(
                eq(KEY_PREFIX + EMAIL),
                eq(OTP),
                eq(OTP_TTL)
        );
    }

    @Test
    @DisplayName("Should retrieve OTP from Redis successfully")
    void shouldRetrieveOtpFromRedisSuccessfully() {
        // given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(KEY_PREFIX + EMAIL)).thenReturn(OTP);

        // when
        String result = otpCacheService.getOtp(EMAIL);

        // then
        assertThat(result).isEqualTo(OTP);
        verify(redisTemplate).opsForValue();
        verify(valueOperations).get(KEY_PREFIX + EMAIL);
    }

    @Test
    @DisplayName("Should return null when OTP does not exist in Redis")
    void shouldReturnNullWhenOtpDoesNotExistInRedis() {
        // given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(KEY_PREFIX + EMAIL)).thenReturn(null);

        // when
        String result = otpCacheService.getOtp(EMAIL);

        // then
        assertThat(result).isNull();
        verify(redisTemplate).opsForValue();
        verify(valueOperations).get(KEY_PREFIX + EMAIL);
    }

    @Test
    @DisplayName("Should delete OTP from Redis successfully")
    void shouldDeleteOtpFromRedisSuccessfully() {
        // given
        when(redisTemplate.delete(KEY_PREFIX + EMAIL)).thenReturn(true);

        // when
        otpCacheService.deleteOtp(EMAIL);

        // then
        verify(redisTemplate).delete(KEY_PREFIX + EMAIL);
    }

    @Test
    @DisplayName("Should handle delete when OTP key does not exist")
    void shouldHandleDeleteWhenOtpKeyDoesNotExist() {
        // given
        when(redisTemplate.delete(KEY_PREFIX + EMAIL)).thenReturn(false);

        // when
        otpCacheService.deleteOtp(EMAIL);

        // then
        verify(redisTemplate).delete(KEY_PREFIX + EMAIL);
    }

    @Test
    @DisplayName("Should save OTP with correct key prefix")
    void shouldSaveOtpWithCorrectKeyPrefix() {
        // given
        String differentEmail = "jane.smith@example.com";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // when
        otpCacheService.saveOtp(differentEmail, OTP);

        // then
        verify(valueOperations).set(
                eq(KEY_PREFIX + differentEmail),
                eq(OTP),
                any(Duration.class)
        );
    }

    @Test
    @DisplayName("Should retrieve OTP with correct key prefix")
    void shouldRetrieveOtpWithCorrectKeyPrefix() {
        // given
        String differentEmail = "jane.smith@example.com";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(KEY_PREFIX + differentEmail)).thenReturn(OTP);

        // when
        String result = otpCacheService.getOtp(differentEmail);

        // then
        assertThat(result).isEqualTo(OTP);
        verify(valueOperations).get(KEY_PREFIX + differentEmail);
    }

    @Test
    @DisplayName("Should delete OTP with correct key prefix")
    void shouldDeleteOtpWithCorrectKeyPrefix() {
        // given
        String differentEmail = "jane.smith@example.com";

        // when
        otpCacheService.deleteOtp(differentEmail);

        // then
        verify(redisTemplate).delete(KEY_PREFIX + differentEmail);
    }

    @Test
    @DisplayName("Should save different OTPs for different emails")
    void shouldSaveDifferentOtpsForDifferentEmails() {
        // given
        String email1 = "user1@example.com";
        String email2 = "user2@example.com";
        String otp1 = "111111";
        String otp2 = "222222";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // when
        otpCacheService.saveOtp(email1, otp1);
        otpCacheService.saveOtp(email2, otp2);

        // then
        verify(valueOperations).set(eq(KEY_PREFIX + email1), eq(otp1), any(Duration.class));
        verify(valueOperations).set(eq(KEY_PREFIX + email2), eq(otp2), any(Duration.class));
    }

    @Test
    @DisplayName("Should return empty string when OTP is empty in Redis")
    void shouldReturnEmptyStringWhenOtpIsEmptyInRedis() {
        // given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(KEY_PREFIX + EMAIL)).thenReturn("");

        // when
        String result = otpCacheService.getOtp(EMAIL);

        // then
        assertThat(result).isEmpty();
        verify(valueOperations).get(KEY_PREFIX + EMAIL);
    }
}

