package com.practice.sprintfour_notetakers.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfo {
    private Long id;
    private String email;
    private String fullName;
    private String role;
}
