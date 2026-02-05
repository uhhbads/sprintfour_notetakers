package com.practice.sprintfour_notetakers.dto.auth;

import jakarta.validation.constraints.NotBlank;

public class RefreshTokenRequest {
    @NotBlank
    private String refreshToken;
}
