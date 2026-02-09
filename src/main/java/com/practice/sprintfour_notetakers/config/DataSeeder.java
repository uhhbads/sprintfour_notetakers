package com.practice.sprintfour_notetakers.config;

import com.practice.sprintfour_notetakers.entity.User;
import com.practice.sprintfour_notetakers.entity.Role;
import com.practice.sprintfour_notetakers.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByEmail("admin@example.com")) {
                User admin = new User();
                admin.setEmail("admin@example.com");
                admin.setPassword(passwordEncoder.encode("admin123")); // known password
                admin.setRole(Role.ADMIN);
                admin.setFullName("Admin User");
                userRepository.save(admin);
                System.out.println("Admin user created: admin@example.com / admin123");
            }
        };
    }
}
