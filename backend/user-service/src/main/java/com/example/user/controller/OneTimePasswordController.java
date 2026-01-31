package com.example.user.controller;

import com.example.user.domain.dto.otp.request.OtpRequest;
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

    @PostMapping
    public ResponseEntity<String> sendOtp(@RequestBody @Valid OtpRequest otpRequest) {
        otpService.processOtpRequest(otpRequest.getEmail().toLowerCase().trim());

        return ResponseEntity.ok("OTP has been sent to your email");
    }
}