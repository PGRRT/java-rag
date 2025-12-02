package com.example.user.service;

import com.example.user.utility.OtpCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final StringRedisTemplate redisTemplate;
    private final OtpCodeGenerator otpCodeGenerator;
    private static final Duration OTP_TTL = Duration.ofMinutes(5);

    /**
     * Generate OTP for the given email and store in Redis with TTL
     */
    public String generateOtp(String email) {
        String otp = otpCodeGenerator.generateOtp(6);

        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set("otp:" + email, otp, OTP_TTL);
        return otp;
    }

    /**
     * Verify OTP
     */
    public boolean verifyOtp(String email, String otp) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String savedOtp = ops.get("otp:" + email);

        if (savedOtp == null || !savedOtp.equals(otp)) {
            return false;
        }

        redisTemplate.delete("otp:" + email);
        return true;
    }
}
