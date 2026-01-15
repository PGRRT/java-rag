package com.example.user.service.impl;

import com.example.common.jwt.dto.AccessRefreshToken;
import com.example.common.jwt.dto.JwtUserClaims;
import com.example.common.jwt.dto.UserPrincipal;
import com.example.user.domain.dto.auth.AuthResult;
import com.example.user.domain.dto.user.request.LoginUserRequest;
import com.example.user.domain.dto.user.request.RegisterUserRequest;
import com.example.user.domain.dto.user.response.UserResponse;
import com.example.user.domain.entities.User;
import com.example.user.exceptions.InvalidTokenException;
import com.example.user.exceptions.OtpInvalidException;
import com.example.user.exceptions.TokenRefreshException;
import com.example.user.exceptions.UserNotActiveException;
import com.example.user.repository.UserRepository;
import com.example.common.jwt.service.JwtService;
import com.example.user.service.AuthService;
import com.example.user.service.OtpService;
import com.example.user.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.common.jwt.service.CookieService;
import java.time.Duration;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final CookieService cookieService;
    private final OtpService otpService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResult loginUser(LoginUserRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        String role = principal.authorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.replace("ROLE_", ""))
                .findFirst()
                .orElse("USER");

        UserResponse userResponse = UserResponse.builder()
                .id(principal.id())
                .email(principal.email())
                .role(role)
                .active(principal.isEnabled())
                .build();

        AccessRefreshToken tokens = jwtService.createSessionCookies(
                userResponse.getId(),
                userResponse.getEmail(),
                userResponse.getRole()
        );

        return new AuthResult(userResponse, tokens.getAccessToken(), tokens.getRefreshToken());
    }

    @Override
    public AuthResult registerUser(RegisterUserRequest request) {
        boolean hasOtpValid = otpService.verifyOtp(request.getEmail(), request.getOtp());

        if (!hasOtpValid) {
            throw new OtpInvalidException("Invalid or expired OTP. Please request a new one.");
        }

        UserResponse userResponse = userService.saveUser(request);

        AccessRefreshToken tokens = jwtService.createSessionCookies(
                userResponse.getId(),
                userResponse.getEmail(),
                userResponse.getRole()
        );

        return new AuthResult(userResponse, tokens.getAccessToken(), tokens.getRefreshToken());
    }

    @Override
    public String refreshToken(String refreshTokenCookie) {
        try {
            // Validate the refresh token
            if (refreshTokenCookie == null || refreshTokenCookie.isEmpty()) {
                throw new InvalidTokenException("Refresh token is missing");
            }

            Claims claims = jwtService.getClaims(refreshTokenCookie);

            String subjectId = claims.getSubject();
            String jti = claims.getId();
            long expiration = claims.getExpiration().getTime();

//          Check if the jti is blacklisted
            if (isJtiBlacklisted(jti)) {
                throw new InvalidTokenException("Refresh token is blacklisted");
            }

            UUID userId = UUID.fromString(subjectId);

            User user = userRepository.findUserWithRoleById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            if (!user.isActive()) {
                blacklistToken(expiration, jti);
                throw new UserNotActiveException("User account is not active");
            }

            JwtUserClaims jwtUserClaims = JwtUserClaims.builder()
                    .userId(user.getId())
                    .email(user.getEmail())
                    .role(user.getRole().getName())
                    .build();

            return jwtService.generateAccessToken(jwtUserClaims);

        } catch (ExpiredJwtException e) {
            String tokenPreview = refreshTokenCookie != null && refreshTokenCookie.length() > 20
                ? refreshTokenCookie.substring(0, 20) + "..."
                : "null or short token";
            log.warn("Refresh token expired for token: {}", tokenPreview);
            throw new InvalidTokenException("Refresh token expired");
        } catch (JwtException e) {
            log.warn("Invalid refresh token: {}", e.getMessage());
            throw new InvalidTokenException("Invalid refresh token");
        } catch (InvalidTokenException | UserNotActiveException e) {
            log.warn("Token refresh failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error refreshing token", e);
            throw new TokenRefreshException("Failed to refresh token");
        }
    }

    private boolean isJtiBlacklisted(String jti) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + jti));
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        Claims claims = jwtService.getClaims(token);
        String jti = claims.getId();

        return redisTemplate.hasKey("blacklist:" + jti);
    }

    @Override
    public void blacklistToken(long expiration, String jti) {
        long ttl = expiration - System.currentTimeMillis();
        if (ttl > 0 && jti != null && !jti.isEmpty()) {
           redisTemplate.opsForValue().set(
                   "blacklist:" + jti,
                   "blacklisted",
                   Duration.ofMillis(ttl));
        }
    }

    @Override
    public void getClaimsAndBlacklistToken(String token) {
        try {
            // Get token expiration time
            Claims claims = jwtService.getClaims(token);

            long expiration = claims.getExpiration().getTime();
            String jti = claims.getId();

            blacklistToken(expiration, jti);
        } catch (Exception e) {
            log.warn("Failed to blacklist token", e);
        }
    }

    @Override
    public ResponseCookie logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isEmpty()) {
            getClaimsAndBlacklistToken(refreshToken);
        }

        return cookieService.clearRefreshTokenCookie();
    }
}
