package com.practice.sprintfour_notetakers.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AuthController.class) // Only loads the AuthController and related MVC beans
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testRegisterEndpoint() throws Exception {
        String userJson = """
            {"email":"test@example.com", "password":"password123", "name":"Test User"}
            """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated());
    }
}

