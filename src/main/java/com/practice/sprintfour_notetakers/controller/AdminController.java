package com.practice.sprintfour_notetakers.controller;

import com.practice.sprintfour_notetakers.dto.admin.SystemStats;
import com.practice.sprintfour_notetakers.dto.auth.UserInfo;
import com.practice.sprintfour_notetakers.entity.User;
import com.practice.sprintfour_notetakers.repository.NoteRepository;
import com.practice.sprintfour_notetakers.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserRepository userRepository;
    private final NoteRepository noteRepository;

    public AdminController(UserRepository userRepository, NoteRepository noteRepository) {
        this.userRepository = userRepository;
        this.noteRepository = noteRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserInfo>> getAllUsers(){
        List<UserInfo> users = userRepository.findAll()
                .stream()
                .map(this::mapToUserInfo)
                .toList();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/stats")
    public ResponseEntity<SystemStats> getStats(){
        SystemStats stats = new SystemStats();
        stats.setTotalUsers(userRepository.count());
        stats.setTotalNotes(noteRepository.count());
        stats.setNotesToday(noteRepository.countByCreatedAtAfter(LocalDate.now().atStartOfDay()));

        return ResponseEntity.ok(stats);
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
