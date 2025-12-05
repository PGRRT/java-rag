package com.example.user.service;

import com.example.user.domain.dto.user.request.LoginUserRequest;
import com.example.user.domain.dto.user.request.RegisterUserRequest;
import com.example.user.domain.dto.user.response.UserResponse;
import com.example.user.domain.entities.Role;
import com.example.user.domain.entities.User;
import com.example.user.mapper.UserMapper;
import com.example.user.repository.UserRepository;
import com.example.user.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final JwtService jwtService;

    @Transactional
    public UserResponse saveUser(RegisterUserRequest registerUserRequest, boolean hasOtpValid) {
        if (!registerUserRequest.getPassword().equals(registerUserRequest.getConfirmPassword())) {
            throw new IllegalArgumentException("Password and Confirm Password do not match");
        } else if (userRepository.findByEmail(registerUserRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }

        User user = userMapper.toEntity(registerUserRequest); // email and password
        if (hasOtpValid) {
            user.setEmailVerified(true);
        }

        Role defaultRole = roleService.getDefaultRole();
        user.setRole(defaultRole);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

        return userMapper.toDto(user);
    }

    public UserResponse getCurrentUser(String accessToken) {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new BadCredentialsException("Access token is missing");
        }

        String email = null;

        if (!accessToken.startsWith("Bearer ")) {
            throw new BadCredentialsException("Invalid access token format");
        }
        String token = accessToken.substring(7); // Remove "Bearer " prefix

        try {
            email = jwtService.getEmailFromToken(token);
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid access token");
        }

        if (email == null || email.isEmpty()) {
            throw new BadCredentialsException("Invalid access token");
        }

        User user = userRepository.findUserWithRoleByEmail(email).orElse(null);
        if (user == null) {
            throw new BadCredentialsException("User not found");
        } else if (!user.isActive()) {
            throw new IllegalStateException("User account is not active"); // skip for now
        }

        return userMapper.toDto(user);
    }

    public UserResponse loginUser(LoginUserRequest registerRequestDto) {
        User user = userRepository.findUserWithRoleByEmail(registerRequestDto.getEmail()).orElse(null);
        // User user =
        // userRepository.findByEmail(registerRequestDto.getEmail()).orElse(null);
        if (user == null || !passwordEncoder.matches(registerRequestDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        } else if (!user.isActive()) {
            throw new IllegalStateException("User account is not active"); // skip for now
        }

        return userMapper.toDto(user);
    }

}
