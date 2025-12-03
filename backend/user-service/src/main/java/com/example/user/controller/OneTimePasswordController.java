package com.example.user.controller;

import com.example.user.domain.dto.otp.OtpRequest;
import com.example.user.service.EmailService;
import com.example.user.service.OtpCacheService;
import com.example.user.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/otp")
@RequiredArgsConstructor
public class OneTimePasswordController {
    private final OtpService otpService;
    private final EmailService emailService;
    private final OtpCacheService otpCacheService;

    @PostMapping
    public ResponseEntity<String> createOtp(@RequestBody @Valid OtpRequest otpRequest) {
        String email = otpRequest.getEmail();
        String otp = otpService.generateOtp(email);
        otpCacheService.saveOtp(email, otp);

        emailService.sendRegistrationEmail(email,otp);

        return ResponseEntity.ok("OTP has been sent to your email");
    }
}