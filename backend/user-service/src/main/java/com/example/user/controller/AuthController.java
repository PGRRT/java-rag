package com.example.user.controller;

import com.example.common.jwt.dto.AccessRefreshToken;
import com.example.user.domain.dto.auth.AuthResult;
import com.example.user.domain.dto.auth.response.AuthResponse;
import com.example.user.domain.dto.auth.response.UserWithCookie;
import com.example.user.domain.dto.user.request.LoginUserRequest;
import com.example.user.domain.dto.user.request.RegisterUserRequest;
import com.example.user.domain.dto.user.response.UserResponse;
import com.example.user.exceptions.OtpInvalidException;
import com.example.user.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse<UserResponse>> login(@RequestBody @Valid LoginUserRequest loginUserRequest) {
        AuthResult result = authService.loginUser(loginUserRequest);

        AuthResponse<UserResponse> authResponse = AuthResponse.<UserResponse>builder()
                .accessToken(result.accessToken())
                .user(result.userResponse())
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, result.refreshTokenCookie().toString())
                .body(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(
            @CookieValue(required = false, name = "refreshToken") String refreshToken) {

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is missing");
        }

        String accessToken = authService.refreshToken(refreshToken);

        return ResponseEntity.ok().body(accessToken);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse<UserResponse>> register(@RequestBody @Valid RegisterUserRequest registerUserRequest) {
        AuthResult result = authService.registerUser(registerUserRequest);

        AuthResponse<UserResponse> authResponse = AuthResponse.<UserResponse>builder()
                .accessToken(result.accessToken())
                .user(result.userResponse())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, result.refreshTokenCookie().toString())
                .body(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        ResponseCookie clearCookie = authService.logout(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                .build();
    }

}
