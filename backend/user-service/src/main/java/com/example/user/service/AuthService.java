package com.example.user.service;

import com.example.user.domain.dto.auth.AuthResult;
import com.example.user.domain.dto.user.request.LoginUserRequest;
import com.example.user.domain.dto.user.request.RegisterUserRequest;
import org.springframework.http.ResponseCookie;

public interface AuthService {
    AuthResult loginUser(LoginUserRequest request);
    AuthResult registerUser(RegisterUserRequest request);
    String refreshToken(String refreshTokenCookie);
    boolean isTokenBlacklisted(String token);
    void blacklistToken(long expiration, String jti);
    void getClaimsAndBlacklistToken(String token);
    ResponseCookie logout(String refreshToken);
}
