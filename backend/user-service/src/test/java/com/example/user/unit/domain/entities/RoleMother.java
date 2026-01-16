package com.example.user.unit.domain.entities;

import com.example.user.domain.entities.Role;

public class RoleMother {
    public static Role.RoleBuilder userRole() {
        return Role.builder()
                .id(1L)
                .name("USER");
    }

    public static Role.RoleBuilder moderatorRole() {
        return Role.builder()
                .id(2L)
                .name("MODERATOR");
    }

    public static Role.RoleBuilder adminRole() {
        return Role.builder()
                .id(3L)
                .name("ADMIN");
    }
}
