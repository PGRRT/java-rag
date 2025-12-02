package com.example.user.utility;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class OtpCodeGenerator {

    private static final String DIGITS = "0123456789";
    private final SecureRandom random = new SecureRandom();

    /**
     * Generates an OTP code with the given length.
     * @param length length of the OTP code
     * @return OTP code as a String
     */
    public String generateOtp(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("OTP length must be greater than 0");
        }

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(DIGITS.length());
            sb.append(DIGITS.charAt(index));
        }

        return sb.toString();
    }
}