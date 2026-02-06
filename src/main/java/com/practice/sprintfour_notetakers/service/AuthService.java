package com.practice.sprintfour_notetakers.service;

import com.practice.sprintfour_notetakers.dto.auth.AuthResponse;
import com.practice.sprintfour_notetakers.dto.auth.LoginRequest;
import com.practice.sprintfour_notetakers.dto.auth.RegisterRequest;
import com.practice.sprintfour_notetakers.dto.auth.UserInfo;
import com.practice.sprintfour_notetakers.entity.RefreshToken;
import com.practice.sprintfour_notetakers.entity.Role;
import com.practice.sprintfour_notetakers.entity.User;
import com.practice.sprintfour_notetakers.exception.EmailAlreadyExistsException;
import com.practice.sprintfour_notetakers.exception.InvalidCredentialsException;
import com.practice.sprintfour_notetakers.exception.InvalidRefreshTokenException;
import com.practice.sprintfour_notetakers.repository.RefreshTokenRepository;
import com.practice.sprintfour_notetakers.repository.UserRepository;
import com.practice.sprintfour_notetakers.security.JwtUtil;
import jakarta.validation.constraints.Email;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new EmailAlreadyExistsException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());

        user = userRepository.save(user);

        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), String.valueOf(user.getRole()));
        String refreshTokenValue = jwtUtil.generateRefreshToken(user.getEmail());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenValue);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));

        refreshTokenRepository.save(refreshToken);

        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshTokenValue);
        response.setExpiresIn(jwtUtil.getAccessTokenExpirationSeconds());
        response.setUser(mapToUserInfo(user));

        return response;
    }

    public AuthResponse login(LoginRequest request){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                        .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), String.valueOf(user.getRole()));
        String refreshTokenValue = jwtUtil.generateRefreshToken(user.getEmail());

        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(refreshTokenValue);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));

        refreshTokenRepository.save(refreshToken);

        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshTokenValue);
        response.setExpiresIn(jwtUtil.getAccessTokenExpirationSeconds());
        response.setUser(mapToUserInfo(user));

        return response;
    }

    public AuthResponse refreshToken(String refreshToken){
        RefreshToken foundRefreshToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if(jwtUtil.isTokenExpired(refreshToken)){
            refreshTokenRepository.delete(foundRefreshToken);
            throw new InvalidRefreshTokenException("Refresh token expired");
        }

        User user = foundRefreshToken.getUser();
        String newAccessToken = jwtUtil.generateAccessToken(user.getEmail(), String.valueOf(user.getRole()));

        AuthResponse response = new AuthResponse();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(jwtUtil.getAccessTokenExpirationSeconds());
        response.setUser(mapToUserInfo(user));

        return response;
    }

    private UserInfo mapToUserInfo(User user) {
        UserInfo info = new UserInfo();
        info.setId(user.getId());
        info.setEmail(user.getEmail());
        info.setFullName(user.getFullName());
        info.setRole(user.getRole().name());
        return info;
    }

}
