package com.example.user.service.impl;

import com.example.user.service.EmailService;
import com.example.user.service.OtpCacheService;
import com.example.user.service.OtpService;
import com.example.user.utility.OtpCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {
    private final OtpCodeGenerator otpCodeGenerator;
    private final OtpCacheService otpCacheService;
    private final EmailService emailService;

    @Override
    public void processOtpRequest(String email) {
        String otp = generateOtp(email);
        otpCacheService.saveOtp(email, otp);

        emailService.sendRegistrationEmail(email,otp);
    }

    @Override
    public String generateOtp(String email) {
        String otp = otpCodeGenerator.generateOtp(6);

        return otp;
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        String savedOtp = otpCacheService.getOtp(email);

        if (savedOtp == null || !savedOtp.equals(otp)) {
            return false;
        }

        otpCacheService.deleteOtp(email);
        return true;
    }
}
