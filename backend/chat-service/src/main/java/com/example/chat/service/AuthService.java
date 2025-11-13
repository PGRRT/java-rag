//package com.example.medai.services;
//
//import com.signaro.backend.domain.dto.auth.AccessRefreshToken;
//import com.signaro.backend.domain.dto.auth.response.UserWithCookiesResponse;
//import com.signaro.backend.domain.dto.user.response.UserResponse;
//import com.signaro.backend.domain.entities.Attribute;
//import com.signaro.backend.domain.entities.User;
//import com.signaro.backend.exceptions.InvalidTokenException;
//import com.signaro.backend.exceptions.TokenRefreshException;
//import com.signaro.backend.exceptions.UserNotActiveException;
//import com.signaro.backend.mappers.UserMapper;
//import com.signaro.backend.repositories.UserRepository;
//import com.signaro.backend.security.JwtService;
//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.JwtException;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.time.Duration;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class AuthService {
//
//    private final JwtService jwtService;
//    private final UserRepository userRepository;
//    private final CookieService cookieService;
//    private final RedisTemplate<String, String> redisTemplate;
//    private final UserMapper userMapper;
//
//    public UserWithCookiesResponse refreshToken(String refreshTokenCookie) {
//        try {
//            // Validate the refresh token
//            if (refreshTokenCookie == null || refreshTokenCookie.isEmpty()) {
//                throw new JwtException("Refresh token is missing");
//            }
//
//            // Check if the token is expired
//            if (jwtService.isTokenExpired(refreshTokenCookie)) {
//                throw new JwtException("Invalid refresh token");
//            }
//
//            // Check if the token is blacklisted
//            if (isTokenBlacklisted(refreshTokenCookie)) {
//                throw new InvalidTokenException("Refresh token is blacklisted");
//            }
//
//            String email = jwtService.getEmailFromToken(refreshTokenCookie);
//            if (email == null || email.isEmpty()) {
//                throw new InvalidTokenException("Invalid refresh token");
//            }
//
//            User user = userRepository.findUserWithRoleAndAttributesByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//            if (!user.isActive() || !user.isEmailVerified()) {
//                throw new UserNotActiveException("User account is not active");
//            }
//
//            blacklistToken(refreshTokenCookie);
//
//            AccessRefreshToken sessionCookies = jwtService.createSessionCookies(
//                    user.getId(),
//                    user.getEmail(),
//                    user.getRole().getName(),
//                    user.getAttributes().stream().map(Attribute::getName).toList()
//            );
//
//            UserResponse userResponse = userMapper.toDto(user);
//
//            return UserWithCookiesResponse.builder()
//                    .accessToken(sessionCookies.getAccessToken())
//                    .refreshToken(sessionCookies.getRefreshToken())
//                    .user(userResponse)
//                    .build();
//        }  catch (ExpiredJwtException e) {
//            log.warn("Refresh token expired for token: {}", refreshTokenCookie.substring(0, 20) + "...");
//            throw new InvalidTokenException("Refresh token expired");
//        } catch (JwtException e) {
//            log.warn("Invalid refresh token: {}", e.getMessage());
//            throw new InvalidTokenException("Invalid refresh token");
//        } catch (Exception e) {
//            log.error("Error refreshing token", e);
//            throw new TokenRefreshException("Failed to refresh token");
//        }
//    }
//
//    public boolean isTokenBlacklisted(String token) {
//        return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + token));
//    }
//
//    public void blacklistToken(String token) {
//        try {
//            // Get token expiration time
//            long expiration = jwtService.getTokenExpiration(token);
//            long ttl = expiration - System.currentTimeMillis();
//
//            if (ttl > 0) {
//                redisTemplate.opsForValue().set(
//                        "blacklist:" + token,
//                        "blacklisted",
//                        Duration.ofMillis(ttl)
//                );
//            }
//        } catch (Exception e) {
//            log.warn("Failed to blacklist token", e);
//        }
//    }
//
//    public void logout(String accessToken, String refreshToken) {
//        if (accessToken != null && !accessToken.isEmpty()) {
//            blacklistToken(accessToken);
//        }
//        if (refreshToken != null && !refreshToken.isEmpty()) {
//            blacklistToken(refreshToken);
//        }
//
//
//    }
//}
