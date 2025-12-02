package com.example.user.domain.dto.auth;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record JwtUserClaims(UUID userId, String email, String role) {
}
