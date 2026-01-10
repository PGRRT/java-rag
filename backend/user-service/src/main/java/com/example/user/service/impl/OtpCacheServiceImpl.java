package com.example.user.service.impl;

import com.example.user.service.OtpCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class OtpCacheServiceImpl implements OtpCacheService {

    private final StringRedisTemplate redisTemplate;
    private static final Duration OTP_TTL = Duration.ofMinutes(5);
    private static final String KEY_PREFIX = "otp:";

    @Override
    public void saveOtp(String email, String otp) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(KEY_PREFIX + email, otp, OTP_TTL);
    }

    @Override
    public String getOtp(String email) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        return ops.get(KEY_PREFIX + email);
    }

    @Override
    public void deleteOtp(String email) {
        redisTemplate.delete(KEY_PREFIX + email);
    }
}
