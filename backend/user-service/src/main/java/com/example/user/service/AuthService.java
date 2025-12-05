package com.example.user.service;

import com.example.user.domain.dto.auth.AccessRefreshToken;
import com.example.user.domain.dto.auth.response.UserWithCookie;
import com.example.user.domain.dto.user.response.UserResponse;
import com.example.user.domain.entities.User;
import com.example.user.exceptions.InvalidTokenException;
import com.example.user.exceptions.TokenRefreshException;
import com.example.user.exceptions.UserNotActiveException;
import com.example.user.mapper.UserMapper;
import com.example.user.repository.UserRepository;
import com.example.user.security.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final CookieService cookieService;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserMapper userMapper;

    public UserWithCookie refreshToken(String refreshTokenCookie) {
        try {
            // Validate the refresh token
            if (refreshTokenCookie == null || refreshTokenCookie.isEmpty()) {
                throw new JwtException("Refresh token is missing");
            }

            // Check if the token is expired
            if (jwtService.isTokenExpired(refreshTokenCookie)) {
                throw new JwtException("Invalid refresh token");
            }

            // Check if the token is blacklisted
            if (isTokenBlacklisted(refreshTokenCookie)) {
                throw new InvalidTokenException("Refresh token is blacklisted");
            }

            String email = jwtService.getEmailFromToken(refreshTokenCookie);
            if (email == null || email.isEmpty()) {
                throw new InvalidTokenException("Invalid refresh token");
            }

            User user = userRepository.findUserWithRoleByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            if (!user.isActive() || !user.isEmailVerified()) {
                throw new UserNotActiveException("User account is not active");
            }

            blacklistToken(refreshTokenCookie);

            AccessRefreshToken sessionCookies = jwtService.createSessionCookies(
                    user.getId(),
                    user.getEmail(),
                    user.getRole().getName());

            UserResponse userResponse = userMapper.toDto(user);

            return UserWithCookie.builder()
                    .accessToken(sessionCookies.getAccessToken())
                    .refreshToken(sessionCookies.getRefreshToken())
                    .user(userResponse)
                    .build();
        } catch (ExpiredJwtException e) {
            log.warn("Refresh token expired for token: {}", refreshTokenCookie.substring(0, 20) + "...");
            throw new InvalidTokenException("Refresh token expired");
        } catch (JwtException e) {
            log.warn("Invalid refresh token: {}", e.getMessage());
            throw new InvalidTokenException("Invalid refresh token");
        } catch (Exception e) {
            log.error("Error refreshing token", e);
            throw new TokenRefreshException("Failed to refresh token");
        }
    }

    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + token));
    }

    public void blacklistToken(String token) {
        try {
            // Get token expiration time
            long expiration = jwtService.getTokenExpiration(token);
            long ttl = expiration - System.currentTimeMillis();

            if (ttl > 0) {
                redisTemplate.opsForValue().set(
                        "blacklist:" + token,
                        "blacklisted",
                        Duration.ofMillis(ttl));
            }
        } catch (Exception e) {
            log.warn("Failed to blacklist token", e);
        }
    }

    public void logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isEmpty()) {
            blacklistToken(refreshToken);
        }

    }
}
