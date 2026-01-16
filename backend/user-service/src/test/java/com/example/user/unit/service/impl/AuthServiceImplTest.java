package com.example.user.unit.service.impl;

import com.example.common.jwt.dto.AccessRefreshToken;
import com.example.common.jwt.dto.UserPrincipal;
import com.example.common.jwt.service.CookieService;
import com.example.common.jwt.service.JwtService;
import com.example.user.domain.dto.auth.AuthResult;
import com.example.user.domain.dto.user.request.LoginUserRequest;
import com.example.user.domain.dto.user.request.RegisterUserRequest;
import com.example.user.domain.dto.user.response.UserResponse;
import com.example.user.domain.entities.Role;
import com.example.user.service.impl.AuthServiceImpl;
import com.example.user.unit.domain.entities.RoleMother;
import com.example.user.domain.entities.User;
import com.example.user.unit.domain.entities.UserMother;
import com.example.user.exceptions.InvalidTokenException;
import com.example.user.exceptions.OtpInvalidException;
import com.example.user.exceptions.TokenRefreshException;
import com.example.user.exceptions.UserNotActiveException;
import com.example.user.repository.UserRepository;
import com.example.user.service.OtpService;
import com.example.user.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    @Mock private JwtService jwtService;
    @Mock private OtpService otpService;
    @Mock private UserService userService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserRepository userRepository;
    @Mock private CookieService cookieService;
    @Mock private RedisTemplate<String, String> redisTemplate;
    @Mock private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private AuthServiceImpl authService;

    // Test Data Constants
    private static final String EMAIL = "john.doe@example.com";
    private static final String PASSWORD = "pass123";
    private static final String OTP = "123456";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String ROLE = "USER";

    // Generated tokens for testing
//    private  String ACCESS_TOKEN;
//    private  String REFRESH_TOKEN;

    private static final String ACCESS_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiNDZlYmQxMC1lOTE2LTRjNWMtYmJmNS01YTA2ZmIxZmVjYWIiLCJlbWFpbCI6ImJrdWJhMTQwMUBnbWFpbC5jb20iLCJyb2xlIjoiVVNFUiIsInR5cGUiOiJhY2Nlc3MiLCJpYXQiOjE3Njg1OTE2OTEsImV4cCI6MTc2ODU5MjU5MSwiaXNzIjoic2lnbmFyby5jb20iLCJqdGkiOiIyMzAxMmZjMy1kZDgzLTQ5OGYtYWVmYS01NTkyNGUzMWNmZjUifQ.7SCqMnRU9a8AhpseM32sPn_Omx0QHYG2UJPr8tdhYTIROK9-1hboHbFpfWFmRhx9hpxH3x01AvTq2nCJyKR-Og";
    private static final String REFRESH_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiNDZlYmQxMC1lOTE2LTRjNWMtYmJmNS01YTA2ZmIxZmVjYWIiLCJlbWFpbCI6ImJrdWJhMTQwMUBnbWFpbC5jb20iLCJyb2xlIjoiVVNFUiIsInR5cGUiOiJyZWZyZXNoIiwiaWF0IjoxNzY4NTkxNTg2LCJleHAiOjE3NjkxOTYzODYsImlzcyI6InNpZ25hcm8uY29tIiwianRpIjoiODZkYjAwYjQtYmRmMC00NDI0LTgyMTMtMDU3NWQ2NjNmNDdhIn0.zxkvHhWsYtdL6dxt3oWNkQsTFtDYYf_sHgDSOr9bA6H5JaJFAhHhQmaHCGvmvcVULNerm231-usuA3a7GI6kDg";

    private static final Claims REFRESH_CLAIMS = Jwts.claims()
            .subject(USER_ID.toString())
            .id(UUID.randomUUID().toString())
            .add("type", "refresh")
            .expiration(new Date(System.currentTimeMillis() + 100000))
            .build();



    @Test
    @DisplayName("Should login user successfully and return tokens")
    void shouldLoginUser() {
        // given
        LoginUserRequest request = LoginUserRequest.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();

        // Mock Authentication
        UserPrincipal principal = new UserPrincipal(USER_ID, EMAIL, PASSWORD, true, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + ROLE)));
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(auth);

        // Mock Token Generation
        ResponseCookie mockCookie = ResponseCookie.from("refresh_token", "cookie-value-123").build();
        AccessRefreshToken tokens = new AccessRefreshToken(ACCESS_TOKEN, mockCookie);

        when(jwtService.createSessionCookies(USER_ID, EMAIL, ROLE)).thenReturn(tokens);

        // when
        AuthResult result = authService.loginUser(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.accessToken()).isEqualTo(ACCESS_TOKEN);
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

        verify(userService, never()).saveUser(any());
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
        when(userService.saveUser(request)).thenReturn(mockUserResponse);

        // Cookie setup
        ResponseCookie mockCookie = ResponseCookie.from("refresh_token", "cookie-reg-123").build();
        AccessRefreshToken tokens = new AccessRefreshToken(ACCESS_TOKEN, mockCookie);

        when(jwtService.createSessionCookies(USER_ID, EMAIL, ROLE)).thenReturn(tokens);

        // when
        AuthResult result = authService.registerUser(request);

        // then
        assertThat(result.userResponse().getEmail()).isEqualTo(EMAIL);
        assertThat(result.refreshTokenCookie().getValue()).isEqualTo("cookie-reg-123");

        // Verify interaction
        verify(userService).saveUser(request);
    }

    @Test
    @DisplayName("Should refresh token successfully")
    void shouldRefreshTokenSuccessfully() {
        // given
        Role role = RoleMother.userRole().build();

        User mockUser = User.builder()
                .id(USER_ID)
                .email(EMAIL)
                .active(true)
                .role(role)
                .build();

        when(jwtService.getClaims(REFRESH_TOKEN)).thenReturn(REFRESH_CLAIMS);

        // Mock Token Generation
        when(jwtService.generateAccessToken(any())).thenReturn(ACCESS_TOKEN);

        // 3. Check Blacklist in Redis
        when(redisTemplate.hasKey("blacklist:" + REFRESH_CLAIMS.getId())).thenReturn(false);

        // 4. Mock User Retrieval
        when(userRepository.findUserWithRoleById(USER_ID)).thenReturn(Optional.of(mockUser));

        // when
        String accessToken = authService.refreshToken(REFRESH_TOKEN);

        // then
        assertThat(accessToken).isEqualTo(ACCESS_TOKEN);
    }

    @Test
    @DisplayName("Should throw exception when refresh token is blacklisted")
    void shouldThrowWhenRefreshTokenIsBlacklisted() {
        // given
        when(jwtService.getClaims(REFRESH_TOKEN)).thenReturn(REFRESH_CLAIMS);

        when(redisTemplate.hasKey("blacklist:" + REFRESH_CLAIMS.getId())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.refreshToken(REFRESH_TOKEN))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Refresh token is blacklisted");

        verify(userRepository, never()).findUserWithRoleById(any());
    }

    @Test
    @DisplayName("Should throw exception when user is not active during refresh")
    void shouldThrowWhenUserNotActive() {
        // given
        User inactiveUser = UserMother.inactive().build();

        when(jwtService.getClaims(REFRESH_TOKEN)).thenReturn(REFRESH_CLAIMS);
        when(redisTemplate.hasKey(anyString())).thenReturn(false);
        when(userRepository.findUserWithRoleById(USER_ID)).thenReturn(Optional.of(inactiveUser));

        // Mock Redis operations
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // when & then
        assertThatThrownBy(() -> authService.refreshToken(REFRESH_TOKEN))
                .isInstanceOf(UserNotActiveException.class);

        // Check if we blacklisted the token
        verify(valueOperations).set(eq("blacklist:" + REFRESH_CLAIMS.getId()), anyString(), any(Duration.class));
    }

    @Test
    @DisplayName("Should logout user successfully")
    void shouldLogoutUser() {
        // given
        // Mock getting claims from token
        when(jwtService.getClaims(REFRESH_TOKEN)).thenReturn(REFRESH_CLAIMS);

        // Mock Redis operations
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Mock cookie clearing
        ResponseCookie emptyCookie = ResponseCookie.from("refresh_token", "").maxAge(0).build();
        when(cookieService.clearRefreshTokenCookie()).thenReturn(emptyCookie);

        // when
        ResponseCookie result = authService.logout(REFRESH_TOKEN);

        // then
        assertThat(result.getMaxAge().getSeconds()).isEqualTo(0); // Check cookie is cleared

        // check if token was blacklisted
        verify(valueOperations).set(
                eq("blacklist:" + REFRESH_CLAIMS.getId()),
                eq("blacklisted"),
                any(Duration.class)
        );
    }

    @Test
    @DisplayName("Should logout successfully even with null refresh token")
    void shouldLogoutWithNullToken() {
        // given
        String refreshToken = null;

        // Mock cookie clearing
        ResponseCookie emptyCookie = ResponseCookie.from("refresh_token", "").maxAge(0).build();
        when(cookieService.clearRefreshTokenCookie()).thenReturn(emptyCookie);

        // when
        ResponseCookie result = authService.logout(refreshToken);

        // then
        assertThat(result.getMaxAge().getSeconds()).isEqualTo(0);

        // Verify that getClaimsAndBlacklistToken was not called (branch coverage)
        verify(jwtService, never()).getClaims(anyString());
    }

    @Test
    @DisplayName("Should logout successfully even with empty refresh token")
    void shouldLogoutWithEmptyToken() {
        // given
        String refreshToken = "";

        // Mock cookie clearing
        ResponseCookie emptyCookie = ResponseCookie.from("refresh_token", "").maxAge(0).build();
        when(cookieService.clearRefreshTokenCookie()).thenReturn(emptyCookie);

        // when
        ResponseCookie result = authService.logout(refreshToken);

        // then
        assertThat(result.getMaxAge().getSeconds()).isEqualTo(0);

        // Verify that getClaimsAndBlacklistToken was not called
        verify(jwtService, never()).getClaims(anyString());
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when refresh token is null")
    void shouldThrowWhenRefreshTokenIsNull() {
        // when & then
        assertThatThrownBy(() -> authService.refreshToken(null))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Refresh token is missing");

        verify(jwtService, never()).getClaims(anyString());
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when refresh token is empty")
    void shouldThrowWhenRefreshTokenIsEmpty() {
        // when & then
        assertThatThrownBy(() -> authService.refreshToken(""))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Refresh token is missing");

        verify(jwtService, never()).getClaims(anyString());
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when refresh token has expired")
    void shouldThrowWhenRefreshTokenExpired() {
        // given
        when(jwtService.getClaims(REFRESH_TOKEN)).thenThrow(new ExpiredJwtException(null, null, "Token expired"));

        // when & then
        assertThatThrownBy(() -> authService.refreshToken(REFRESH_TOKEN))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Refresh token expired");
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when refresh token is malformed")
    void shouldThrowWhenRefreshTokenIsMalformed() {
        // given
        when(jwtService.getClaims(REFRESH_TOKEN)).thenThrow(new JwtException("Invalid JWT"));

        // when & then
        assertThatThrownBy(() -> authService.refreshToken(REFRESH_TOKEN))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Invalid refresh token");
    }

    @Test
    @DisplayName("Should throw TokenRefreshException when unexpected error occurs")
    void shouldThrowTokenRefreshExceptionOnUnexpectedError() {
        // given
        when(jwtService.getClaims(REFRESH_TOKEN)).thenThrow(new RuntimeException("Database error"));

        // when & then
        assertThatThrownBy(() -> authService.refreshToken(REFRESH_TOKEN))
                .isInstanceOf(TokenRefreshException.class)
                .hasMessage("Failed to refresh token");
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user not found during refresh")
    void shouldThrowWhenUserNotFoundDuringRefresh() {
        // given
        when(jwtService.getClaims(REFRESH_TOKEN)).thenReturn(REFRESH_CLAIMS);
        when(redisTemplate.hasKey("blacklist:" + REFRESH_CLAIMS.getId())).thenReturn(false);
        when(userRepository.findUserWithRoleById(USER_ID)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.refreshToken(REFRESH_TOKEN))
                .isInstanceOf(TokenRefreshException.class)
                .hasMessage("Failed to refresh token");
    }

    @Test
    @DisplayName("Should handle expired token with short token string for preview")
    void shouldHandleExpiredTokenWithShortString() {
        // given
        String refreshToken = "short";

        when(jwtService.getClaims(refreshToken)).thenThrow(new ExpiredJwtException(null, null, "Token expired"));

        // when & then
        assertThatThrownBy(() -> authService.refreshToken(refreshToken))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Refresh token expired");
    }

    @Test
    @DisplayName("Should return true when token is blacklisted")
    void shouldReturnTrueWhenTokenIsBlacklisted() {
        // given
        when(jwtService.getClaims(REFRESH_TOKEN)).thenReturn(REFRESH_CLAIMS);
        when(redisTemplate.hasKey("blacklist:" + REFRESH_CLAIMS.getId())).thenReturn(true);

        // when
        boolean result = authService.isTokenBlacklisted(REFRESH_TOKEN);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when token is not blacklisted")
    void shouldReturnFalseWhenTokenIsNotBlacklisted() {
        // given
        when(jwtService.getClaims(REFRESH_TOKEN)).thenReturn(REFRESH_CLAIMS);
        when(redisTemplate.hasKey("blacklist:" + REFRESH_CLAIMS.getId())).thenReturn(false);

        // when
        boolean result = authService.isTokenBlacklisted(REFRESH_TOKEN);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false when token is null")
    void shouldReturnFalseWhenTokenIsNull() {
        // when
        boolean result = authService.isTokenBlacklisted(null);

        // then
        assertThat(result).isFalse();
        verify(jwtService, never()).getClaims(anyString());
    }

    @Test
    @DisplayName("Should return false when token is empty")
    void shouldReturnFalseWhenTokenIsEmpty() {
        // when
        boolean result = authService.isTokenBlacklisted("");

        // then
        assertThat(result).isFalse();
        verify(jwtService, never()).getClaims(anyString());
    }


    @Test
    @DisplayName("Should blacklist token when TTL is positive")
    void shouldBlacklistTokenWhenTtlIsPositive() {
        // given
        long expiration = System.currentTimeMillis() + 100000;
        String jti = UUID.randomUUID().toString();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // when
        authService.blacklistToken(expiration, jti);

        // then
        verify(valueOperations).set(
                eq("blacklist:" + jti),
                eq("blacklisted"),
                any(Duration.class)
        );
    }

    @Test
    @DisplayName("Should not blacklist token when TTL is zero or negative")
    void shouldNotBlacklistTokenWhenTtlIsNegative() {
        // given
        long expiration = System.currentTimeMillis() - 100000; // expired
        String jti = UUID.randomUUID().toString();

        // when
        authService.blacklistToken(expiration, jti);

        // then
        verify(redisTemplate, never()).opsForValue();
    }

    @Test
    @DisplayName("Should not blacklist token when jti is null")
    void shouldNotBlacklistTokenWhenJtiIsNull() {
        // given
        long expiration = System.currentTimeMillis() + 100000;
        String jti = null;

        // when
        authService.blacklistToken(expiration, jti);

        // then
        verify(redisTemplate, never()).opsForValue();
    }

    @Test
    @DisplayName("Should not blacklist token when jti is empty")
    void shouldNotBlacklistTokenWhenJtiIsEmpty() {
        // given
        long expiration = System.currentTimeMillis() + 100000;
        String jti = "";

        // when
        authService.blacklistToken(expiration, jti);

        // then
        verify(redisTemplate, never()).opsForValue();
    }

    @Test
    @DisplayName("Should extract claims and blacklist token successfully")
    void shouldExtractClaimsAndBlacklistTokenSuccessfully() {
        // given
        when(jwtService.getClaims(REFRESH_TOKEN)).thenReturn(REFRESH_CLAIMS);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // when
        authService.getClaimsAndBlacklistToken(REFRESH_TOKEN);

        // then
        verify(jwtService).getClaims(REFRESH_TOKEN);
        verify(valueOperations).set(
                eq("blacklist:" + REFRESH_CLAIMS.getId()),
                eq("blacklisted"),
                any(Duration.class)
        );
    }

    @Test
    @DisplayName("Should handle exception gracefully when extracting claims fails")
    void shouldHandleExceptionWhenExtractingClaimsFails() {
        // given
        String token = "invalid.token";

        when(jwtService.getClaims(token)).thenThrow(new JwtException("Invalid token"));

        // when - should not throw exception
        authService.getClaimsAndBlacklistToken(token);

        // then
        verify(jwtService).getClaims(token);
        verify(redisTemplate, never()).opsForValue();
    }

    @Test
    @DisplayName("Should handle exception when claims parsing throws runtime exception")
    void shouldHandleRuntimeExceptionDuringClaimsParsing() {
        // given
        when(jwtService.getClaims(REFRESH_TOKEN)).thenThrow(new RuntimeException("Unexpected error"));

        // when - should not throw exception
        authService.getClaimsAndBlacklistToken(REFRESH_TOKEN);

        // then
        verify(jwtService).getClaims(REFRESH_TOKEN);
        verify(redisTemplate, never()).opsForValue();
    }
}