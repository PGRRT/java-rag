package com.example.user.service.impl;

import com.example.common.jwt.dto.AccessRefreshToken;
import com.example.common.jwt.dto.UserPrincipal;
import com.example.common.jwt.service.CookieService;
import com.example.common.jwt.service.JwtService;
import com.example.user.domain.dto.auth.AuthResult;
import com.example.user.domain.dto.user.request.LoginUserRequest;
import com.example.user.domain.dto.user.request.RegisterUserRequest;
import com.example.user.domain.dto.user.response.UserResponse;
import com.example.user.exceptions.OtpInvalidException;
import com.example.user.repository.UserRepository;
import com.example.user.service.OtpService;
import com.example.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    @Mock private JwtService jwtService;
    @Mock private UserRepository userRepository;
    @Mock private RedisTemplate<String, String> redisTemplate;
    @Mock private CookieService cookieService;
    @Mock private OtpService otpService;
    @Mock private UserService userService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    // Test Data Constants
    private final String EMAIL = "test@example.com";
    private final String PASSWORD = "pass123";
    private final String OTP = "123456";
    private final UUID USER_ID = UUID.randomUUID();
    private final String ROLE = "USER";
    @Test
    @DisplayName("Should login user successfully and return tokens")
    void shouldLoginUser() {
        // given
        LoginUserRequest request = LoginUserRequest.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();

        // Mock Authentication
        UserPrincipal principal = new UserPrincipal(USER_ID, EMAIL, PASSWORD, true, Collections.singletonList(new SimpleGrantedAuthority("ROLE" + ROLE)));
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(auth);

        // Mock Token Generation
        ResponseCookie mockCookie = ResponseCookie.from("refresh_token", "cookie-value-123").build();
        AccessRefreshToken tokens = new AccessRefreshToken("access-token-jwt", mockCookie);

        when(jwtService.createSessionCookies(USER_ID, EMAIL, ROLE)).thenReturn(tokens);

        // when
        AuthResult result = authService.loginUser(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.accessToken()).isEqualTo("access-token-jwt");
        assertThat(result.userResponse().getEmail()).isEqualTo(EMAIL);

        // Verify refresh token cookie
        assertThat(result.refreshTokenCookie()).isNotNull();
        assertThat(result.refreshTokenCookie().getValue()).isEqualTo("cookie-value-123");
    }

    @Test
    @DisplayName("Should throw exception when OTP is invalid during registration")
    void shouldThrowWhenOtpInvalid() {
        // given
        RegisterUserRequest request = RegisterUserRequest.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .confirmPassword(PASSWORD)
                .otp(OTP)
                .build();

        when(otpService.verifyOtp(EMAIL, OTP)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.registerUser(request))
                .isInstanceOf(OtpInvalidException.class)
                .hasMessageContaining("Invalid or expired OTP");

        verify(userService, never()).saveUser(any(), anyBoolean());
        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("Should register user when OTP is valid")
    void shouldRegisterUserWhenOtpValid() {
        // given
        RegisterUserRequest request = RegisterUserRequest.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .confirmPassword(PASSWORD)
                .otp(OTP)
                .build();

        UserResponse mockUserResponse = UserResponse.builder()
                .id(USER_ID)
                .email(EMAIL)
                .role(ROLE)
                .build();

        when(otpService.verifyOtp(EMAIL, OTP)).thenReturn(true);
        when(userService.saveUser(request, true)).thenReturn(mockUserResponse);

        // Cookie setup
        ResponseCookie mockCookie = ResponseCookie.from("refresh_token", "cookie-reg-123").build();
        AccessRefreshToken tokens = new AccessRefreshToken("access-token-reg", mockCookie);

        when(jwtService.createSessionCookies(USER_ID, EMAIL, ROLE)).thenReturn(tokens);

        // when
        AuthResult result = authService.registerUser(request);

        // then
        assertThat(result.userResponse().getEmail()).isEqualTo(EMAIL);
        assertThat(result.refreshTokenCookie().getValue()).isEqualTo("cookie-reg-123");

        // Verify interaction
        verify(userService).saveUser(request, true);
    }
}