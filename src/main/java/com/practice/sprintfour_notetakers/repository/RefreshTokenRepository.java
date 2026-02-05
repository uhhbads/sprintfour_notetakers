package com.practice.sprintfour_notetakers.repository;

import com.practice.sprintfour_notetakers.entity.RefreshToken;
import com.practice.sprintfour_notetakers.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
