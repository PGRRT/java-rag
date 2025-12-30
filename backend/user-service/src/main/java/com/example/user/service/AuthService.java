package com.example.user.service;

import com.example.common.jwt.dto.JwtUserClaims;
import com.example.user.domain.entities.User;
import com.example.user.exceptions.InvalidTokenException;
import com.example.user.exceptions.TokenRefreshException;
import com.example.user.exceptions.UserNotActiveException;
import com.example.user.mapper.UserMapper;
import com.example.user.repository.UserRepository;
import com.example.common.jwt.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.common.jwt.service.CookieService;
import java.time.Duration;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public String refreshToken(String refreshTokenCookie) {
        try {
            // Validate the refresh token
            if (refreshTokenCookie == null || refreshTokenCookie.isEmpty()) {
                throw new JwtException("Refresh token is missing");
            }

            Claims claims = jwtService.getClaims(refreshTokenCookie);

            String subjectId = claims.getSubject();
            String jti = claims.getId();
            long expiration = claims.getExpiration().getTime();

//             Check if the jti is blacklisted
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

            String token = jwtService.generateAccessToken(jwtUserClaims);

            return token;

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

    private boolean isJtiBlacklisted(String jti) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + jti));
    }

    public boolean isTokenBlacklisted(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        Claims claims = jwtService.getClaims(token);
        String jti = claims.getId();

        return redisTemplate.hasKey("blacklist:" + jti);
    }

    public void blacklistToken(long expiration, String jti) {
        long ttl = expiration - System.currentTimeMillis();
        if (ttl > 0 && jti != null && !jti.isEmpty()) {
           redisTemplate.opsForValue().set(
                   "blacklist:" + jti,
                   "blacklisted",
                   Duration.ofMillis(ttl));
        }
    }

    public void blacklistToken(String token) {
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

    public void logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isEmpty()) {
            blacklistToken(refreshToken);
        }
    }
}
