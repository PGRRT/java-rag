package com.example.user.service;

import com.example.user.repository.RoleRepository;
import com.example.user.domain.entities.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role getDefaultRole() {
        return roleRepository.findByName("USER").orElseThrow(() -> new IllegalStateException("Default role not found"));
    }
}
