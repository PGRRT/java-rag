package com.example.user.unit.domain.entities;

import com.example.user.domain.entities.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserMother {

    private UserMother() {
    }

    public static User.UserBuilder complete() {
        return User.builder()
                .id(UUID.randomUUID())
                .email("john.doe@example.com")
                .password("pass123")
                .emailVerified(true)
                .active(true)
                .role(RoleMother.userRole().build())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now());
    }

    public static User.UserBuilder unverified() {
        return complete()
                .emailVerified(false)
                .email("unverified@example.com");
    }

    public static User.UserBuilder inactive() {
        return complete()
                .active(false);
    }

    public static User.UserBuilder admin() {
        return complete()
                .role(RoleMother.adminRole().build());
    }
}
