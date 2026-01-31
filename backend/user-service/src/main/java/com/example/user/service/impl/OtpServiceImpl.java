package com.example.user.service.impl;

import com.example.user.exceptions.EmailAlreadyTakenException;
import com.example.user.service.*;
import com.example.user.utility.NormalizeEmail;
import com.example.user.utility.OtpCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {
    private final OtpCodeGenerator otpCodeGenerator;
    private final OtpCacheService otpCacheService;
    private final EmailService emailService;
    private final UserService userService;

    @Override
    public void processOtpRequest(String email) {
        String normalizedEmail = NormalizeEmail.normalize(email);

        if (!userService.isEmailAvailable(normalizedEmail)) {
            throw new EmailAlreadyTakenException("Email is already in use");
        }

        String otp = otpCodeGenerator.generateOtp(6);
        otpCacheService.saveOtp(normalizedEmail, otp);

        emailService.sendRegistrationEmail(normalizedEmail,otp);
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        String normalizedEmail = NormalizeEmail.normalize(email);

        String savedOtp = otpCacheService.getOtp(normalizedEmail);

        if (savedOtp == null || !savedOtp.equals(otp)) {
            return false;
        }

        otpCacheService.deleteOtp(normalizedEmail);
        return true;
    }
}
