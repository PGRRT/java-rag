package com.example.common.jwt.service;

import com.example.common.jwt.dto.JwtUserClaims;
import com.example.common.jwt.dto.AccessRefreshToken;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Getter
@Setter
@RequiredArgsConstructor
public class JwtService {
    private final CookieService cookieService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.accessTokenExpirationMs}")
    private int jwtAccessTokenExpirationMs;

    @Value("${jwt.refreshTokenExpirationMs}")
    private int jwtRefreshTokenExpirationMs;

    protected Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public String getEmailFromToken(String token) {
        return (String) getClaims(token).get("email");
    }

    public String getRoleFromToken(String token) {
        return (String) getClaims(token).get("role");
    }

    public Set<String> getAttributesFromToken(String token) {
        Object attributes = getClaims(token).get("attributes");
        if (attributes instanceof Set<?> attrs) {
            return attrs.stream().map(Object::toString).collect(Collectors.toSet());
        }
        return Set.of();
    }

    public String getIdFromToken(String token) {
        return getClaims(token).getId();
    }

    public boolean isTokenInvalid(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (SecurityException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return true;
    }

    // New methods from UserJwtService

    public AccessRefreshToken createSessionCookies(
            UUID id,
            String email,
            String role) {
        JwtUserClaims jwtUserClaims = getJwtUserClaims(
                id,
                email,
                role
        );

        String accessToken = getAccessToken(jwtUserClaims);
        ResponseCookie refreshTokenCookie = getRefreshTokenCookie(jwtUserClaims);

        return AccessRefreshToken.builder().accessToken(accessToken).refreshToken(refreshTokenCookie).build();
    }

    public JwtUserClaims getJwtUserClaims(UUID id, String email, String role) {
        return JwtUserClaims.builder()
                .userId(id)
                .email(email)
                .role(role)
                .build();
    }

    private String generateToken(JwtUserClaims userClaims, int expirationMs, String type) {
        return Jwts.builder()
                .subject(userClaims.userId().toString())
                .claim("email", userClaims.email())
                .claim("role", userClaims.role())
                .claim("type", type)
                .issuedAt(new java.util.Date(System.currentTimeMillis()))
                .expiration(new java.util.Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .issuer("signaro.com")
                .id(UUID.randomUUID().toString())
                .compact();
    }

    public String generateAccessToken(JwtUserClaims userClaims) {
        return generateToken(userClaims, jwtAccessTokenExpirationMs, "access");
    }

    public String generateRefreshToken(JwtUserClaims userClaims) {
        return generateToken(userClaims, jwtRefreshTokenExpirationMs, "refresh");
    }

    public String getAccessToken(JwtUserClaims jwtUserClaims) {
        try {
            return generateAccessToken(jwtUserClaims);
        } catch (JwtException e) {
            log.error("Error generating access token", e);
            throw new IllegalStateException("Cannot generate access token");
        }
    }

    public ResponseCookie getRefreshTokenCookie(JwtUserClaims jwtUserClaims) {
        try {
            String refreshToken = generateRefreshToken(jwtUserClaims);

            return cookieService.createCookie("refreshToken", refreshToken, getJwtRefreshTokenExpirationMs());
        } catch (JwtException e) {
            log.error("Error generating refresh token", e);
            throw new IllegalStateException("Cannot generate access token");
        }
    }
}

