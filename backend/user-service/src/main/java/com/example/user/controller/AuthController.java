//package com.example.medai.controllers;
//
//import com.signaro.backend.domain.dto.attributes.response.AttributeResponse;
//import com.signaro.backend.domain.dto.auth.AccessRefreshToken;
//import com.signaro.backend.domain.dto.auth.response.UserWithCookiesResponse;
//import com.signaro.backend.domain.dto.user.request.LoginUserRequest;
//import com.signaro.backend.domain.dto.user.request.RegisterUserRequest;
//import com.signaro.backend.domain.dto.user.response.UserResponse;
//import com.signaro.backend.exceptions.OtpInvalidException;
//import com.signaro.backend.security.JwtService;
//import com.signaro.backend.services.AuthService;
//import com.signaro.backend.services.CookieService;
//import com.signaro.backend.services.OtpService;
//import com.signaro.backend.services.UserService;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseCookie;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping(path = "/api/v1/auth")
//@RequiredArgsConstructor
//public class AuthController {
//    private final UserService userService;
//    private final JwtService jwtService;
//    private final OtpService otpService;
//    private final AuthService authService;
//    private final CookieService cookieService;
//
//    @GetMapping("/me")
//    public ResponseEntity<UserResponse> getCurrentUser(@CookieValue(name = "accessToken", required = false) String accessToken) {
//        UserResponse currentUser = userService.getCurrentUser(accessToken);
//        return ResponseEntity.ok(currentUser);
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<UserResponse> login(@RequestBody @Valid LoginUserRequest loginUserRequest, HttpServletResponse response) {
//        UserResponse userResponse = userService.loginUser(loginUserRequest);
//
//        AccessRefreshToken sessionCookies = jwtService.createSessionCookies(
//                userResponse.getId(),
//                userResponse.getEmail(),
//                userResponse.getRole(),
//                userResponse.getAttributes().stream().map(AttributeResponse::getName).toList()
//        );
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.SET_COOKIE, sessionCookies.getAccessToken().toString())
//                .header(HttpHeaders.SET_COOKIE, sessionCookies.getRefreshToken().toString())
//                .body(userResponse);
//    }
//
//    @PostMapping("/refresh")
//    public ResponseEntity<UserResponse> refresh(
//            @CookieValue(required = false, name = "refreshToken") String refreshToken) {
//
//        UserWithCookiesResponse userWithCookiesResponse = authService.refreshToken(refreshToken);
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.SET_COOKIE, userWithCookiesResponse.getAccessToken().toString())
//                .header(HttpHeaders.SET_COOKIE, userWithCookiesResponse.getRefreshToken().toString())
//                .body(userWithCookiesResponse.getUser());
//    }
//
//    @PostMapping("/register")
//    public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterUserRequest registerUserRequest, HttpServletResponse response) {
//        boolean hasOtpValid = otpService.verifyOtp(registerUserRequest.getEmail(), registerUserRequest.getOtp());
//
//        if (!hasOtpValid) {
//            throw new OtpInvalidException("Invalid or expired OTP. Please request a new one.");
//        }
//
//        UserResponse userResponse = userService.saveUser(registerUserRequest,true);
//
//        AccessRefreshToken sessionCookies = jwtService.createSessionCookies(
//                userResponse.getId(),
//                userResponse.getEmail(),
//                userResponse.getRole(),
//                userResponse.getAttributes().stream().map(AttributeResponse::getName).toList()
//        );
//
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .header(HttpHeaders.SET_COOKIE, sessionCookies.getAccessToken().toString())
//                .header(HttpHeaders.SET_COOKIE, sessionCookies.getRefreshToken().toString())
//                .body(userResponse);
//    }
//
//    @PostMapping("/logout")
//    public ResponseEntity<Void> logout(
//            @CookieValue(name = "accessToken", required = false) String accessToken,
//            @CookieValue(name = "refreshToken", required = false) String refreshToken
//    ) {
//        authService.logout(accessToken, refreshToken);
//
//        ResponseCookie clearAccess = cookieService.clearAccessTokenCookie();
//        ResponseCookie clearRefresh = cookieService.clearRefreshTokenCookie();
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.SET_COOKIE, clearAccess.toString())
//                .header(HttpHeaders.SET_COOKIE, clearRefresh.toString())
//                .build();
//    }
//
//}
