package com.practice.sprintfour_notetakers.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LoginRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
