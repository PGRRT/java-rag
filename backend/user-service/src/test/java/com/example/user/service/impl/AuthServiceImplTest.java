package com.example.user.service.impl;

import com.example.common.jwt.dto.AccessRefreshToken;
import com.example.common.jwt.dto.UserPrincipal;
import com.example.common.jwt.service.CookieService;
import com.example.common.jwt.service.JwtService;
import com.example.user.domain.dto.auth.AuthResult;
import com.example.user.domain.dto.user.request.LoginUserRequest;
import com.example.user.domain.dto.user.request.RegisterUserRequest;
import com.example.user.domain.dto.user.response.UserResponse;
import com.example.user.domain.entities.Role;
import com.example.user.domain.entities.RoleMother;
import com.example.user.domain.entities.User;
import com.example.user.domain.entities.UserMother;
import com.example.user.exceptions.InvalidTokenException;
import com.example.user.exceptions.OtpInvalidException;
import com.example.user.exceptions.UserNotActiveException;
import com.example.user.repository.UserRepository;
import com.example.user.service.OtpService;
import com.example.user.service.UserService;
import io.jsonwebtoken.Claims;
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
    private final String EMAIL = "john.doe@example.com";
    private final String PASSWORD = "pass123";
    private final String OTP = "123456";
    private final UUID USER_ID = UUID.randomUUID();
    private final String ROLE = "USER";

    // SUCCESSFUL LOGIN TEST
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

    // NEGATIVE REGISTRATION TEST - INVALID OTP
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

    // SUCCESSFUL REGISTRATION TEST
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
        AccessRefreshToken tokens = new AccessRefreshToken("access-token-reg", mockCookie);

        when(jwtService.createSessionCookies(USER_ID, EMAIL, ROLE)).thenReturn(tokens);

        // when
        AuthResult result = authService.registerUser(request);

        // then
        assertThat(result.userResponse().getEmail()).isEqualTo(EMAIL);
        assertThat(result.refreshTokenCookie().getValue()).isEqualTo("cookie-reg-123");

        // Verify interaction
        verify(userService).saveUser(request);
    }

    // REFRESH TOKEN TESTS
    @Test
    @DisplayName("Should refresh token successfully")
    void shouldRefreshTokenSuccessfully() {
        // given
        String refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyXzEyMyIsInR5cGUiOiJyZWZyZXNoIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MzE2MDIyMjJ9.XbPzG4-Xyq5x9b3a_1ZzGq5x9b3a_1ZzGq5x9b3a_1Z";
        String expectedNewAccessToken = "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJzaWduYXJvLmNvbSIsImp0aSI6IjA0YTk4YjU3LTg4M2UtNGU3NS1iZjk3LTUxYmI0MTEzZmY0NyIsInN1YiI6Ijk5IiwiZW1haWwiOiJ1c2VyQGV4YW1wbGUuY29tIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE3MDQ4ODIzMDgsImV4cCI6MTczNjQxODM0OH0.XmZkY2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y";

        Role role = RoleMother.userRole().build();

        User mockUser = User.builder()
                .id(USER_ID)
                .email(EMAIL)
                .active(true)
                .role(role)
                .build();

        Claims claims = Jwts.claims()
                .subject(USER_ID.toString())
                .id("unique-token-id")
                .expiration(new Date(System.currentTimeMillis() + 100000))
                .add("role", ROLE)
                .build();

        when(jwtService.getClaims(refreshToken)).thenReturn(claims);

        // Mock Token Generation
        when(jwtService.generateAccessToken(any())).thenReturn(expectedNewAccessToken);

        // 3. Check Blacklist in Redis
        when(redisTemplate.hasKey("blacklist:unique-token-id")).thenReturn(false);

        // 4. Mock User Retrieval
        when(userRepository.findUserWithRoleById(USER_ID)).thenReturn(Optional.of(mockUser));

        // when
        String accessToken = authService.refreshToken(refreshToken);

        // then
        assertThat(accessToken).isEqualTo(expectedNewAccessToken);
    }

    // NEGATIVE REFRESH TOKEN TESTS - BLACKLISTED TOKEN
    @Test
    @DisplayName("Should throw exception when refresh token is blacklisted")
    void shouldThrowWhenRefreshTokenIsBlacklisted() {
        // given
        String refreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJzaWduYXJvLmNvbSIsImp0aSI6IjA0YTk4YjU3LTg4M2UtNGU3NS1iZjk3LTUxYmI0MTEzZmY0NyIsInN1YiI6Ijk5IiwiZW1haWwiOiJ1c2VyQGV4YW1wbGUuY29tIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE3MDQ4ODIzMDgsImV4cCI6MTczNjQxODM0OH0.XmZkY2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y";

        String id = UUID.randomUUID().toString();
        Claims claims = Jwts.claims()
                .subject(USER_ID.toString())
                .id(id)
                .expiration(new Date(System.currentTimeMillis() + 100000))
                .build();

        when(jwtService.getClaims(refreshToken)).thenReturn(claims);

        // Symulujemy, że Redis ma ten klucz (czyli token JEST na czarnej liście)
        when(redisTemplate.hasKey("blacklist:" + id)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.refreshToken(refreshToken))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Refresh token is blacklisted");

        verify(userRepository, never()).findUserWithRoleById(any());
    }

    // NEGATIVE REFRESH TOKEN TESTS - INACTIVE USER
    @Test
    @DisplayName("Should throw exception when user is not active during refresh")
    void shouldThrowWhenUserNotActive() {
        // given
        String refreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJzaWduYXJvLmNvbSIsImp0aSI6IjA0YTk4YjU3LTg4M2UtNGU3NS1iZjk3LTUxYmI0MTEzZmY0NyIsInN1YiI6Ijk5IiwiZW1haWwiOiJ1c2VyQGV4YW1wbGUuY29tIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE3MDQ4ODIzMDgsImV4cCI6MTczNjQxODM0OH0.XmZkY2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y";

        String id = UUID.randomUUID().toString();
        Claims claims = Jwts.claims()
                .subject(USER_ID.toString())
                .id(id)
                .expiration(new Date(System.currentTimeMillis() + 10000))
                .build();

        User inactiveUser = UserMother.inactive().build();

        when(jwtService.getClaims(refreshToken)).thenReturn(claims);
        when(redisTemplate.hasKey(anyString())).thenReturn(false);
        when(userRepository.findUserWithRoleById(USER_ID)).thenReturn(Optional.of(inactiveUser));

        // Mock Redis operations
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // when & then
        assertThatThrownBy(() -> authService.refreshToken(refreshToken))
                .isInstanceOf(UserNotActiveException.class);

        // Check if we blacklisted the token
        verify(valueOperations).set(eq("blacklist:" + id), anyString(), any(Duration.class));
    }

    // SUCCESSFUL LOGOUT TEST
    @Test
    @DisplayName("Should logout user successfully")
    void shouldLogoutUser() {
        // given
        String refreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJzaWduYXJvLmNvbSIsImp0aSI6IjA0YTk4YjU3LTg4M2UtNGU3NS1iZjk3LTUxYmI0MTEzZmY0NyIsInN1YiI6Ijk5IiwiZW1haWwiOiJ1c2VyQGV4YW1wbGUuY29tIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE3MDQ4ODIzMDgsImV4cCI6MTczNjQxODM0OH0.XmZkY2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y2Y";
        String jti = UUID.randomUUID().toString();

        Claims claims = Jwts.claims()
                .id(jti)
                .expiration(new Date(System.currentTimeMillis() + 100000))
                .build();

        // Mock getting claims from token
        when(jwtService.getClaims(refreshToken)).thenReturn(claims);

        // Mock Redis operations
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Mock cookie clearing
        ResponseCookie emptyCookie = ResponseCookie.from("refresh_token", "").maxAge(0).build();
        when(cookieService.clearRefreshTokenCookie()).thenReturn(emptyCookie);

        // when
        ResponseCookie result = authService.logout(refreshToken);

        // then
        assertThat(result.getMaxAge().getSeconds()).isEqualTo(0); // Check cookie is cleared

        // check if token was blacklisted
        verify(valueOperations).set(
                eq("blacklist:" + jti),
                eq("blacklisted"),
                any(Duration.class)
        );
    }


}