package com.example.user.service;

import com.example.user.domain.dto.otp.request.OtpRequest;

public interface OtpService {
    void processOtpRequest(String email);
    String generateOtp(String email);
    boolean verifyOtp(String email, String otp);
}
