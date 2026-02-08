package com.practice.sprintfour_notetakers.service;

import com.practice.sprintfour_notetakers.dto.auth.RegisterRequest;
import com.practice.sprintfour_notetakers.entity.User;
import com.practice.sprintfour_notetakers.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthServiceTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testPasswordIsHashedOnRegister() throws Exception{
        // 1. Prepare JSON for registration
        String userJson = """
            {
                "email": "test@example.com",
                "password": "password123",
                "name": "Test User"
            }
            """;

        // 2. Perform POST /api/auth/register
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated());  // Verify 201 response

        // 3. Retrieve user from DB
        User savedUser = userRepository.findByEmail("test@example.com").orElseThrow();
        System.out.println(savedUser.getPassword()); // will print hashed password

        // 4. Assert password is hashed
        assertNotEquals("password123", savedUser.getPassword());

        // 5. Optional: verify matches
        assertTrue(passwordEncoder.matches("password123", savedUser.getPassword()));
    }
}
