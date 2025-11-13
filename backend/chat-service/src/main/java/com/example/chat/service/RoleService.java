//package com.example.medai.services;
//
//import com.example.medai.repositories.RoleRepository;
//import com.signaro.backend.domain.entities.Role;
//import com.signaro.backend.repositories.RoleRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class RoleService {
//    private final RoleRepository roleRepository;
//
//    public Role getDefaultRole() {
//        return roleRepository.findByName("USER").orElseThrow(() -> new IllegalStateException("Default role not found"));
//    }
//}
