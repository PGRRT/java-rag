package com.example.user.domain.dto.auth.response;

import com.example.user.domain.dto.user.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseCookie;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWithCookie {
    UserResponse user;
    String accessToken;
    ResponseCookie refreshToken;
}
