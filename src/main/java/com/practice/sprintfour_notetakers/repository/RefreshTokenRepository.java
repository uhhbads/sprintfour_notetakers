package com.practice.sprintfour_notetakers.repository;

import com.practice.sprintfour_notetakers.entity.RefreshToken;
import com.practice.sprintfour_notetakers.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Transactional
    void deleteByUser(User user);

    Optional<RefreshToken> findByUser(User user);
}
