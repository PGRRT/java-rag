package com.example.user.mapper;

import com.example.user.domain.dto.user.request.RegisterUserRequest;
import com.example.user.domain.dto.user.response.UserResponse;
import com.example.user.domain.entities.Role;
import com.example.user.domain.entities.RoleMother;
import com.example.user.domain.entities.User;
import com.example.user.domain.entities.UserMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {
    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    @DisplayName("Should map RegisterUserRequest to User entity")
    void shouldMapToEntity() {
        // given
        RegisterUserRequest request = RegisterUserRequest.builder()
                .email("mapper-test@example.com")
                .password("SecretPass123!")
                .build();

        // when
        User user = mapper.toEntity(request);

        // then
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(request.getEmail());
        assertThat(user.getPassword()).isEqualTo(request.getPassword());

        // Ensure other fields are null or default (unless mapped)
        assertThat(user.getId()).isNull();
    }

    @Test
    @DisplayName("Should map User entity to UserResponse and flatten Role")
    void shouldMapToDto() {
        // given
        User user = UserMother.admin().build();

        // when
        UserResponse response = mapper.toDto(user);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(user.getId());
        assertThat(response.getEmail()).isEqualTo(user.getEmail());

        assertThat(response.getRole()).isEqualTo("ADMIN");
    }
}