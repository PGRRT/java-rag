package com.example.user.domain.dto.user.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseCookie;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    @JsonIgnore
    private UUID id;

    private String email;

    private String role;

    private boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
