package com.practice.sprintfour_notetakers.service;

import com.practice.sprintfour_notetakers.dto.auth.AuthResponse;
import com.practice.sprintfour_notetakers.dto.auth.LoginRequest;
import com.practice.sprintfour_notetakers.dto.auth.RegisterRequest;
import com.practice.sprintfour_notetakers.entity.RefreshToken;
import com.practice.sprintfour_notetakers.entity.Role;
import com.practice.sprintfour_notetakers.entity.User;
import com.practice.sprintfour_notetakers.exception.EmailAlreadyExistsException;
import com.practice.sprintfour_notetakers.exception.InvalidCredentialsException;
import com.practice.sprintfour_notetakers.repository.RefreshTokenRepository;
import com.practice.sprintfour_notetakers.repository.UserRepository;
import com.practice.sprintfour_notetakers.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private JwtUtil jwtUtil;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_hashesPasswordAndReturnsTokens() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setFullName("Test User");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtUtil.generateAccessToken("test@example.com", "USER")).thenReturn("access");
        when(jwtUtil.generateRefreshToken("test@example.com")).thenReturn("refresh");
        when(jwtUtil.getAccessTokenExpirationSeconds()).thenReturn(3600L);

        AuthResponse res = authService.register(request);

        assertEquals("access", res.getAccessToken());
        assertEquals("refresh", res.getRefreshToken());

        ArgumentCaptor<User> cap = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(cap.capture());
        assertEquals("hashed", cap.getValue().getPassword());
        assertNotEquals("password123", cap.getValue().getPassword());
    }

    @Test
    void register_throwsWhenEmailTaken() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("taken@example.com");
        request.setPassword("password123");
        request.setFullName("User");

        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(request));
    }

    @Test
    void login_returnsTokens() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("password123");

        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setPassword("encoded");
        user.setRole(Role.USER);
        user.setFullName("User");
        user.setCreatedAt(LocalDateTime.now());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateAccessToken("user@example.com", "USER")).thenReturn("access");
        when(jwtUtil.generateRefreshToken("user@example.com")).thenReturn("refresh");
        when(jwtUtil.getAccessTokenExpirationSeconds()).thenReturn(3600L);

        AuthResponse res = authService.login(request);

        assertEquals("access", res.getAccessToken());
        verify(refreshTokenRepository).deleteByUser(user);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void login_throwsWhenUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("missing@example.com");
        request.setPassword("password123");

        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }
}