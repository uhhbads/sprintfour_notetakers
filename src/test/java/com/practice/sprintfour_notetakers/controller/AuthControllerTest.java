package com.practice.sprintfour_notetakers.controller;

import com.practice.sprintfour_notetakers.dto.auth.AuthResponse;
import com.practice.sprintfour_notetakers.dto.auth.LoginRequest;
import com.practice.sprintfour_notetakers.dto.auth.RegisterRequest;
import com.practice.sprintfour_notetakers.dto.auth.UserInfo;
import com.practice.sprintfour_notetakers.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(authService);
    }

    @Test
    void postRegister_returnsCreated() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setFullName("Test User");

        AuthResponse stubResponse = buildAuthResponse();
        when(authService.register(request)).thenReturn(stubResponse);

        ResponseEntity<AuthResponse> response = authController.postRegister(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("fake-access", response.getBody().getAccessToken());
        verify(authService).register(request);
    }

    @Test
    void postLogin_returnsOk() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        AuthResponse stubResponse = buildAuthResponse();
        when(authService.login(request)).thenReturn(stubResponse);

        ResponseEntity<AuthResponse> response = authController.postLogin(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("fake-access", response.getBody().getAccessToken());
        assertEquals("fake-refresh", response.getBody().getRefreshToken());
        verify(authService).login(request);
    }

    private AuthResponse buildAuthResponse() {
        AuthResponse response = new AuthResponse();
        response.setAccessToken("fake-access");
        response.setRefreshToken("fake-refresh");
        response.setExpiresIn(3600L);

        UserInfo userInfo = new UserInfo();
        userInfo.setId(1L);
        userInfo.setEmail("test@example.com");
        userInfo.setFullName("Test User");
        userInfo.setRole("USER");

        response.setUser(userInfo);
        return response;
    }
}