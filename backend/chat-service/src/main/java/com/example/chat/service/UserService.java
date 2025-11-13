//package com.example.medai.services;
//
//import com.signaro.backend.domain.dto.user.request.LoginUserRequest;
//import com.signaro.backend.domain.dto.user.request.RegisterUserRequest;
//import com.signaro.backend.domain.dto.user.response.UserResponse;
//import com.signaro.backend.domain.entities.Role;
//import com.signaro.backend.domain.entities.User;
//import com.signaro.backend.mappers.UserMapper;
//import com.signaro.backend.repositories.UserRepository;
//import com.signaro.backend.security.JwtService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class UserService {
//    private final UserRepository userRepository;
//    private final UserMapper userMapper;
//    private final PasswordEncoder passwordEncoder;
//    private final RoleService roleService;
//    private final JwtService jwtService;
//
//    @Transactional
//    public UserResponse saveUser(RegisterUserRequest registerUserRequest, boolean hasOtpValid) {
//        if (!registerUserRequest.getPassword().equals(registerUserRequest.getConfirmPassword())) {
//            throw new IllegalArgumentException("Password and Confirm Password do not match");
//        } else if (userRepository.findByEmail(registerUserRequest.getEmail()).isPresent()) {
//            throw new IllegalArgumentException("Email is already in use");
//        }
//
//        User user = userMapper.toEntity(registerUserRequest); // email and password
//        if (hasOtpValid) {
//            user.setEmailVerified(true);
//        }
//
//        Role defaultRole = roleService.getDefaultRole();
//        user.setRole(defaultRole);
//
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//
//        userRepository.save(user);
//
//        return userMapper.toDto(user);
//    }
//
//    public UserResponse getCurrentUser(String accessToken) {
//        if (accessToken == null || accessToken.isEmpty()) {
//            throw new BadCredentialsException("Access token is missing");
//        }
//
//        String email = null;
//        try {
//            email = jwtService.getEmailFromToken(accessToken);
//        } catch (Exception e) {
//            throw new BadCredentialsException("Invalid access token");
//        }
//
//        if (email == null || email.isEmpty()) {
//            throw new BadCredentialsException("Invalid access token");
//        }
//
//        User user = userRepository.findUserWithRoleAndAttributesByEmail(email).orElse(null);
//        if (user == null) {
//            throw new BadCredentialsException("User not found");
//        } else if (!user.isActive()) {
//            throw new IllegalStateException("User account is not active");    // skip for now
//        }
//
//        return userMapper.toDto(user);
//    }
//
//    public UserResponse loginUser(LoginUserRequest registerRequestDto) {
//        User user = userRepository.findUserWithRoleAndAttributesByEmail(registerRequestDto.getEmail()).orElse(null);
////        User user = userRepository.findByEmail(registerRequestDto.getEmail()).orElse(null);
//        if (user == null || !passwordEncoder.matches(registerRequestDto.getPassword(), user.getPassword())) {
//            throw new BadCredentialsException("Invalid email or password");
//        } else if (!user.isActive()) {
//         throw new IllegalStateException("User account is not active");    // skip for now
//        }
//
//        return userMapper.toDto(user);
//    }
//
//}
