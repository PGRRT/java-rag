package com.example.user.integration.repository;

import com.example.user.domain.entities.Role;
import com.example.user.domain.entities.User;
import com.example.user.config.JpaConfig;
import com.example.user.repository.RoleRepository;
import com.example.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(JpaConfig.class)
class UserRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:17"))
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");


    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepository userRepository;


    private Role userRole = Role.builder()
            .name("USER")
            .build();

    @Autowired
    private RoleRepository roleRepository;


    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        if (roleRepository.findByName("USER").isEmpty()) {
            userRole = Role.builder()
                    .name("USER")
                    .build();
            roleRepository.save(userRole);
        }
    }

    // ...existing code...

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        // given
        User user = User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .role(userRole)
                .active(true)
                .emailVerified(false)
                .build();
        userRepository.saveAndFlush(user);

        // when
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
        assertThat(found.get().isActive()).isTrue();
    }

    @Test
    @DisplayName("Should return empty when user not found by email")
    void shouldReturnEmptyWhenUserNotFoundByEmail() {
        // when
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        // then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should find user with role by email using EntityGraph")
    void shouldFindUserWithRoleByEmail() {
        // given
        User user = User.builder()
                .email("user@example.com")
                .password("password")
                .role(userRole)
                .active(true)
                .build();

        userRepository.saveAndFlush(user);
        // when
        Optional<User> found = userRepository.findUserWithRoleByEmail("user@example.com");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getRole()).isNotNull();
        assertThat(found.get().getRole().getName()).isEqualTo("USER");
    }

    @Test
    @DisplayName("Should find user with role by id using EntityGraph")
    void shouldFindUserWithRoleById() {
        // given
        User user = User.builder()
                .email("user@example.com")
                .password("password")
                .role(userRole)
                .active(true)
                .build();



        User savedUser = userRepository.saveAndFlush(user);
        // when
        Optional<User> found = userRepository.findUserWithRoleById(savedUser.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getRole()).isNotNull();
        assertThat(found.get().getRole().getName()).isEqualTo("USER");
    }

    @Test
    @DisplayName("Should throw exception when saving user with duplicate email")
    void shouldThrowWhenSavingDuplicateEmail() {
        // given
        User firstUser = User.builder()
                .email("duplicate@example.com")
                .password("password1")
                .role(userRole)
                .build();
        userRepository.saveAndFlush(firstUser);

        User duplicateUser = User.builder()
                .email("duplicate@example.com")
                .password("password2")
                .role(userRole)
                .build();

        // when & then
        assertThatThrownBy(() -> {
            userRepository.saveAndFlush(duplicateUser);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Should save and retrieve user with all fields")
    void shouldSaveAndRetrieveUserWithAllFields() {
        // given
        User user = User.builder()
                .email("complete@example.com")
                .password("encodedPassword")
                .role(userRole)
                .active(true)
                .emailVerified(true)
                .build();

        // when
        User savedUser = userRepository.saveAndFlush(user);
        Optional<User> found = userRepository.findById(savedUser.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("complete@example.com");
        assertThat(found.get().isActive()).isTrue();
        assertThat(found.get().isEmailVerified()).isTrue();
        assertThat(found.get().getCreatedAt()).isNotNull();
        assertThat(found.get().getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should update user fields")
    void shouldUpdateUserFields() {
        // given
        User user = User.builder()
                .email("update@example.com")
                .password("password")
                .role(userRole)
                .active(true)
                .emailVerified(false)
                .build();
        User savedUser = userRepository.saveAndFlush(user);

        // when
        savedUser.setEmailVerified(true);
        savedUser.setActive(false);
        userRepository.saveAndFlush(savedUser);

        // then
        Optional<User> updated = userRepository.findById(savedUser.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().isEmailVerified()).isTrue();
        assertThat(updated.get().isActive()).isFalse();
    }

    @Test
    @DisplayName("Should delete user")
    void shouldDeleteUser() {
        // given
        User user = User.builder()
                .email("delete@example.com")
                .password("password")
                .role(userRole)
                .build();
        User savedUser = userRepository.saveAndFlush(user);

        // when
        userRepository.delete(savedUser);
        userRepository.flush();

        // then
        Optional<User> deleted = userRepository.findById(savedUser.getId());
        assertThat(deleted).isEmpty();
    }
}

