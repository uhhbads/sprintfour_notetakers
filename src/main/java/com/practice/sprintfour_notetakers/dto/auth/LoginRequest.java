package com.practice.sprintfour_notetakers.dto.auth;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
