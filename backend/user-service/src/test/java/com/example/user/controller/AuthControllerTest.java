package com.example.user.controller;

import com.example.common.jwt.dto.UserPrincipal;
import com.example.user.domain.dto.auth.AuthResult;
import com.example.user.domain.dto.user.request.LoginUserRequest;
import com.example.user.domain.dto.user.request.RegisterUserRequest;
import com.example.user.domain.dto.user.response.UserResponse;
import com.example.user.exceptions.UserNotFoundException;
import com.example.user.service.AuthService;
import com.example.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.util.UUID;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;


    private final UUID TEST_ID = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11");
    private final String TEST_EMAIL = "john.doe@example.com";
    private final String TEST_PASSWORD = "StrongPassword123!";
    private final String ROLE = "USER";
    private final Boolean ACTIVE = true;
    private final String OTP = "123456";

    private final String ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    private final String REFRESH_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXAC23.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDabEa.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQswxaz";

    @Test
    @DisplayName("POST /register - Should return 201 Created, Access Token and Set-Cookie header")
    void shouldRegisterUser_WhenRequestIsValid() throws Exception {
        // given
        RegisterUserRequest request = new RegisterUserRequest(TEST_EMAIL, TEST_PASSWORD, TEST_PASSWORD, OTP);

        AuthResult authResult = createAuthResult();
        given(authService.registerUser(any(RegisterUserRequest.class))).willReturn(authResult);

        // when & then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()) // Expecting 201 Created
                .andExpect(header().exists(HttpHeaders.SET_COOKIE)) // Validate cookie presence
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("refreshToken=" + REFRESH_TOKEN)))
                .andExpect(jsonPath("$.accessToken").value(ACCESS_TOKEN))
                .andExpect(jsonPath("$.user.id").value(TEST_ID.toString()))
                .andExpect(jsonPath("$.user.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.user.role").value(ROLE))
                .andExpect(jsonPath("$.user.active").value(ACTIVE));

        // Verify service interaction
        then(authService).should(times(1)).registerUser(any(RegisterUserRequest.class));
    }

    @Test
    @DisplayName("POST /register - Should return 400 Bad Request on validation failure")
    void shouldReturn400_WhenRegisterRequestIsInvalid() throws Exception {
        // given - Invalid email and empty password
        RegisterUserRequest invalidRequest = new RegisterUserRequest("not-an-email", "", "", "");

        // when & then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        then(authService).shouldHaveNoInteractions();
    }


    @Test
    @DisplayName("POST /login - Should return 200 OK, Access Token and Set-Cookie")
    void shouldLoginUser_WhenCredentialsAreValid() throws Exception {
        // given
        LoginUserRequest request = new LoginUserRequest(TEST_EMAIL, TEST_PASSWORD);
        AuthResult authResult = createAuthResult();

        given(authService.loginUser(any(LoginUserRequest.class))).willReturn(authResult);

        // when & then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andExpect(jsonPath("$.accessToken").value(ACCESS_TOKEN))
                .andExpect(jsonPath("$.user.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.user.id").value(TEST_ID.toString()));

        then(authService).should(times(1)).loginUser(any(LoginUserRequest.class));
    }

    @Test
    @DisplayName("POST /refresh - Should return new Access Token when Refresh Token cookie is present")
    void shouldRefreshToken_WhenCookieIsPresent() throws Exception {
        // given
        String newAccessToken = "new.jwt.access.token";
        Cookie refreshCookie = new Cookie("refreshToken", REFRESH_TOKEN);

        given(authService.refreshToken(REFRESH_TOKEN)).willReturn(newAccessToken);

        // when & then
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .cookie(refreshCookie)) // Simulating browser sending the HttpOnly cookie
                .andExpect(status().isOk())
                .andExpect(content().string(newAccessToken)); // Refresh typically returns String body

        then(authService).should(times(1)).refreshToken(REFRESH_TOKEN);
    }

    @Test
    @DisplayName("POST /refresh - Should return 401 Unauthorized when Refresh Token cookie is missing")
    void shouldReturn401_WhenRefreshTokenIsMissing() throws Exception {
        // given - No cookie provided in request

        // when & then
        mockMvc.perform(post("/api/v1/auth/refresh"))
                .andExpect(status().isUnauthorized()) // Explicit check for controller's null check logic
                .andExpect(content().string("Refresh token is missing"));

        then(authService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("POST /logout - Should clear cookie and return 200 OK")
    void shouldLogoutUser_AndClearCookie() throws Exception {
        // given
        Cookie refreshCookie = new Cookie("refreshToken", REFRESH_TOKEN);

        // Service returns a "clearing" cookie with Max-Age=0
        ResponseCookie clearCookie = ResponseCookie.from("refreshToken", "")
                .maxAge(0)
                .build();

        given(authService.logout(REFRESH_TOKEN)).willReturn(clearCookie);

        // when & then
        mockMvc.perform(post("/api/v1/auth/logout")
                        .cookie(refreshCookie))
                .andExpect(status().isOk())
                // Assert that the response header attempts to expire the cookie (Max-Age=0)
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Max-Age=0")));

        then(authService).should(times(1)).logout(REFRESH_TOKEN);
    }

    /**
     * Helper to create a fully populated AuthResult object.
     * Prevents code duplication in Login and Register tests.
     */
    private AuthResult createAuthResult() {
        UserResponse userResponse = UserResponse.builder()
                .id(TEST_ID)
                .email(TEST_EMAIL)
                .role("USER")
                .active(true)
                .build();

        ResponseCookie cookie = ResponseCookie.from("refreshToken", REFRESH_TOKEN)
                .httpOnly(true)
                .path("/api/v1/auth")
                .maxAge(24 * 60 * 60)
                .build();

        return new AuthResult(userResponse, ACCESS_TOKEN, cookie);
    }
}
