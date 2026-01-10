package com.example.user.service;

import com.example.user.domain.dto.user.request.RegisterUserRequest;
import com.example.user.domain.dto.user.response.UserResponse;

import java.util.UUID;

public interface UserService {
    UserResponse saveUser(RegisterUserRequest registerUserRequest);
    UserResponse getCurrentUser(UUID userId);
    void deleteCurrentUser(UUID userId);
}
