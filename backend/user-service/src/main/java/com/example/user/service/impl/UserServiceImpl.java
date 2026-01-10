package com.example.user.service.impl;

import com.example.user.domain.dto.user.request.RegisterUserRequest;
import com.example.user.domain.dto.user.response.UserResponse;
import com.example.user.domain.entities.Role;
import com.example.user.domain.entities.User;
import com.example.user.exceptions.UserNotFoundException;
import com.example.user.mapper.UserMapper;
import com.example.user.publisher.UserEventPublisher;
import com.example.user.repository.UserRepository;
import com.example.user.service.RoleService;
import com.example.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final UserEventPublisher userEventPublisher;

    @Override
    @Transactional
    public UserResponse saveUser(RegisterUserRequest registerUserRequest) {
        if (!registerUserRequest.getPassword().equals(registerUserRequest.getConfirmPassword())) {
            throw new IllegalArgumentException("Password and Confirm Password do not match");
        } else if (userRepository.findByEmail(registerUserRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }

        User user = userMapper.toEntity(registerUserRequest); // email and password

        user.setEmailVerified(true);

        Role defaultRole = roleService.getDefaultRole();
        user.setRole(defaultRole);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

        return userMapper.toDto(user);
    }

    @Override
    public UserResponse getCurrentUser(UUID userId) {
        User user = userRepository.findUserWithRoleById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public void deleteCurrentUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        userRepository.delete(user);

        userEventPublisher.publishUserDeleted(userId);
    }

}
