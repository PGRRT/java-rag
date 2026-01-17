package com.example.user.unit.service.impl;

import com.example.user.domain.dto.user.request.RegisterUserRequest;
import com.example.user.domain.dto.user.response.UserResponse;
import com.example.user.domain.entities.Role;
import com.example.user.service.impl.UserServiceImpl;
import com.example.user.unit.domain.entities.RoleMother;
import com.example.user.domain.entities.User;
import com.example.user.unit.domain.entities.UserMother;
import com.example.user.exceptions.UserNotFoundException;
import com.example.user.mapper.UserMapper;
import com.example.user.publisher.UserEventPublisher;
import com.example.user.repository.UserRepository;
import com.example.user.service.RoleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private RoleService roleService;
    @Mock private UserEventPublisher userEventPublisher;

    @InjectMocks
    private UserServiceImpl userService;

    // Argument Captor to verify state mutations before persistence
    @Captor
    private ArgumentCaptor<User> userCaptor;

    // Test Constants
    private static final UUID TEST_USER_ID = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11");
    private static final String TEST_EMAIL = "john.doe@example.com";
    private static final String RAW_PASSWORD = "pass123";
    private static final String ENCODED_PASSWORD = "$2a$10$encodedHashValue";
    private static final String ROLE_USER = "USER";

    @Test
    @DisplayName("Should save user successfully: Encodes password, sets Default Role, and marks Verified")
    void shouldSaveUserSuccessfully_WhenOtpIsValid() {
        // given
        RegisterUserRequest request = RegisterUserRequest.builder()
                .email(TEST_EMAIL)
                .password(RAW_PASSWORD)
                .confirmPassword(RAW_PASSWORD)
                .otp("123456")
                .build();

        // We use UserMother but reset ID to null (as it is new) and Password to raw
        User mappedEntity = UserMother.complete()
                .id(null)
                .password(RAW_PASSWORD)
                .emailVerified(false) // Mapper typically maps basic fields, logic handles verification
                .build();

        Role role = RoleMother.userRole().build();
        UserResponse expectedDto = UserResponse.builder()
                .id(TEST_USER_ID)
                .email(TEST_EMAIL)
                .role(role.getName())
                .active(true)
                .build();

        // Mocks
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty()); // Email is unique
        when(userMapper.toEntity(request)).thenReturn(mappedEntity);
        when(roleService.getDefaultRole()).thenReturn(role);
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userMapper.toDto(any(User.class))).thenReturn(expectedDto);

        // when
        UserResponse result = userService.saveUser(request);

        // then
        assertThat(result).isNotNull();

        // Critical: Verify the entity state passed to save()
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        assertThat(capturedUser.getPassword()).isEqualTo(ENCODED_PASSWORD); // Password must be hashed
        assertThat(capturedUser.getRole()).isEqualTo(role);          // Role must be assigned
        assertThat(capturedUser.isEmailVerified()).isTrue();                // Logic sets this to true
    }


    @Test
    @DisplayName("Should throw exception when Passwords do not match")
    void shouldThrowException_WhenPasswordsMismatch() {
        // given
        RegisterUserRequest request = RegisterUserRequest.builder()
                .email(TEST_EMAIL)
                .password("passwordA")
                .confirmPassword("passwordB") // Mismatch
                .build();

        // when & then
        assertThatThrownBy(() -> userService.saveUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password and Confirm Password do not match");

        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Should throw exception when Email is already taken")
    void shouldThrowException_WhenEmailExists() {
        // given
        RegisterUserRequest request = RegisterUserRequest.builder()
                .email(TEST_EMAIL)
                .password(RAW_PASSWORD)
                .confirmPassword(RAW_PASSWORD)
                .build();

        // Simulate existing user using Mother
        User existingUser = UserMother.complete().email(TEST_EMAIL).build();

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));

        // when & then
        assertThatThrownBy(() -> userService.saveUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email is already in use");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return UserResponse when user exists")
    void shouldGetCurrentUser_WhenExists() {
        // given

        Role userRole = RoleMother.userRole().build();

        User user = UserMother.complete()
                .id(TEST_USER_ID)
                .email(TEST_EMAIL)
                .role(userRole)
                .active(true)
                .build();

        UserResponse expectedDto = UserResponse.builder()
                .id(TEST_USER_ID)
                .email(TEST_EMAIL)
                .role(userRole.getName())
                .active(true)
                .build();

        when(userRepository.findUserWithRoleById(TEST_USER_ID)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(expectedDto);

        // when
        UserResponse result = userService.getCurrentUser(TEST_USER_ID);

        // then
        assertThat(result.getId()).isEqualTo(TEST_USER_ID);
        assertThat(result.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(result.getRole()).isEqualTo(ROLE_USER);
        assertThat(result.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when fetching non-existent user")
    void shouldThrowException_WhenUserNotFound() {
        // given
        when(userRepository.findUserWithRoleById(TEST_USER_ID)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getCurrentUser(TEST_USER_ID))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    @DisplayName("Should delete user and publish event when user exists")
    void shouldDeleteUser_AndPublishEvent() {
        // given
        User user = UserMother.complete().id(TEST_USER_ID).build();

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(user));

        // when
        userService.deleteCurrentUser(TEST_USER_ID);

        // then
        verify(userRepository).delete(user);
        verify(userEventPublisher).publishUserDeleted(TEST_USER_ID); // Ensure event is fired
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when trying to delete non-existent user")
    void shouldThrowException_WhenDeletingMissingUser() {
        // given
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.deleteCurrentUser(TEST_USER_ID))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository, never()).delete(any());
        verifyNoInteractions(userEventPublisher); // Event must NOT be fired
    }
}
