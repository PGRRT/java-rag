package com.example.user.controller;


import com.example.user.domain.dto.user.response.UserResponse;
import com.example.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/users/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            @RequestHeader(value = "Authorization", required = false) String accessToken
    ) {
        UserResponse currentUser = userService.getCurrentUser(accessToken);

        return ResponseEntity.ok(currentUser);
    }
}
