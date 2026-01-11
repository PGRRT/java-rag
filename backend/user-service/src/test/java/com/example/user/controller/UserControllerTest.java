package com.example.user.controller;

import com.example.common.jwt.dto.UserPrincipal;
import com.example.user.domain.dto.user.response.UserResponse;
import com.example.user.exceptions.UserNotFoundException;
import com.example.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.util.UUID;
import java.util.List;


import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters
@ActiveProfiles("test")
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private final UUID TEST_ID = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11");
    private final String TEST_EMAIL = "john.doe@example.com";

    @BeforeEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("GET /users/me - Should return 200 OK and User Response when authenticated")
    void shouldReturnCurrentUser_WhenAuthenticated() throws Exception {
        // given

        // The expected DTO returned by the Service
        UserResponse expectedResponse = UserResponse.builder()
                .id(TEST_ID)
                .email(TEST_EMAIL)
                .role("USER")
                .active(true)
                .build();

        given(userService.getCurrentUser(TEST_ID)).willReturn(expectedResponse);
        mockUserInSecurityContext(TEST_ID, TEST_EMAIL);

        // when & then
        mockMvc.perform(get("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // JsonPath assertions ensure the structure and data are correct
                .andExpect(jsonPath("$.id").value(TEST_ID.toString()))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.active").value(true));

    }

    @Test
    @DisplayName("GET /users/me - Should return 404 Not Found when user does not exist")
    void shouldReturn404_WhenUserNotFound() throws Exception {
        // given
        given(userService.getCurrentUser(TEST_ID))
                .willThrow(new UserNotFoundException("User not found"));

        mockUserInSecurityContext(TEST_ID, TEST_EMAIL);

        // when & then
        mockMvc.perform(get("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /users/me - Should return 204 No Content and call service")
    void shouldDeleteCurrentUser_WhenAuthenticated() throws Exception {
        // given
        mockUserInSecurityContext(TEST_ID, TEST_EMAIL);

        // when & then
        mockMvc.perform(delete("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        then(userService).should(times(1)).deleteCurrentUser(TEST_ID);
    }

    @Test
    @DisplayName("DELETE /users/me - Should Propagate Exception when service fails")
    void shouldPropagateException_WhenServiceFails() throws Exception {
        // given
        mockUserInSecurityContext(TEST_ID, TEST_EMAIL);

        willThrow(new IllegalStateException("Cannot delete user"))
                .given(userService).deleteCurrentUser(TEST_ID);

        // when & then
        mockMvc.perform(delete("/api/v1/users/me"))
                .andExpect(status().isInternalServerError());

        then(userService).should(times(1)).deleteCurrentUser(TEST_ID);
    }


    private void mockUserInSecurityContext(UUID userId, String email) {
        UserPrincipal principal = new UserPrincipal(
                userId,
                email,
                null,
                true,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        Authentication auth = new TestingAuthenticationToken(principal, null, "ROLE_USER");

        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
