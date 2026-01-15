package com.example.user.controller;


import com.example.common.jwt.dto.UserPrincipal;
import com.example.user.domain.dto.user.response.UserResponse;
import com.example.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/users/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UserResponse currentUser = userService.getCurrentUser(principal.id());

        return ResponseEntity.ok(currentUser);
    }

    @DeleteMapping("/users/me")
    public ResponseEntity<Void> deleteCurrentUser(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        userService.deleteCurrentUser(principal.id());

        return ResponseEntity.noContent().build();
    }
}
