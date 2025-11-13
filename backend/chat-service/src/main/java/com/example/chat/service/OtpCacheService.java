//package com.example.medai.services;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
//import org.springframework.stereotype.Service;
//
//import java.time.Duration;
//
//@Service
//@RequiredArgsConstructor
//public class OtpCacheService {
//
//    private final StringRedisTemplate redisTemplate;
//    private static final Duration OTP_TTL = Duration.ofMinutes(5);
//
//    /**
//     * Save OTP in Redis for given email.
//     */
//    public void saveOtp(String email, String otp) {
//        ValueOperations<String, String> ops = redisTemplate.opsForValue();
//        ops.set(email, otp, OTP_TTL);
//    }
//
//    /**
//     * Get OTP for given email.
//     */
//    public String getOtp(String email) {
//        ValueOperations<String, String> ops = redisTemplate.opsForValue();
//        return ops.get(email);
//    }
//
//    /**
//     * Delete OTP after verification
//     */
//    public void deleteOtp(String email) {
//        redisTemplate.delete(email);
//    }
//}
