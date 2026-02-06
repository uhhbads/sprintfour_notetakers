package com.practice.sprintfour_notetakers.service;

import com.practice.sprintfour_notetakers.dto.note.NoteCreateRequest;
import com.practice.sprintfour_notetakers.dto.note.NoteResponse;
import com.practice.sprintfour_notetakers.entity.Note;
import com.practice.sprintfour_notetakers.entity.User;
import com.practice.sprintfour_notetakers.repository.NoteRepository;
import com.practice.sprintfour_notetakers.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public NoteService(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    public NoteResponse createNote(NoteCreateRequest request, String userEmail){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Note note = new Note();
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setUser(user);
        note.setCreatedAt(LocalDateTime.now());
        note.setUpdatedAt(LocalDateTime.now());

        user.addNote(note);
        noteRepository.save(note);

        return mapToNoteResponse(note);
    }



    private NoteResponse mapToNoteResponse(Note note){
        NoteResponse noteResponse = new NoteResponse();
        noteResponse.setTitle(note.getTitle());
        noteResponse.setContent(note.getContent());
        noteResponse.setCreatedAt(note.getCreatedAt());
        noteResponse.setUpdatedAt(note.getUpdatedAt());

        return noteResponse;
    }
}
