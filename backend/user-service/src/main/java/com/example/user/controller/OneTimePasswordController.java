//package com.example.medai.controllers;
//
//import com.signaro.backend.domain.dto.otp.OtpRequest;
//import com.signaro.backend.services.EmailService;
//import com.signaro.backend.services.OtpCacheService;
//import com.signaro.backend.services.OtpService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/v1/user")
//@RequiredArgsConstructor
//public class OneTimePasswordController {
//    private final OtpService otpService;
//    private final EmailService emailService;
//    private final OtpCacheService otpCacheService;
//
//    @PostMapping("/create-otp")
//    public ResponseEntity<String> createOtp(@RequestBody @Valid OtpRequest otpRequest) {
//        String email = otpRequest.getEmail();
//        String otp = otpService.generateOtp(email);
//        otpCacheService.saveOtp(email, otp);
//
//        emailService.sendRegistrationEmail(email,otp);
//
//        return ResponseEntity.ok("OTP has been sent to your email");
//    }
//}