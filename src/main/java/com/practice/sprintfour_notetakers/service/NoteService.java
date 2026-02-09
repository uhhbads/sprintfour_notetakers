package com.practice.sprintfour_notetakers.service;

import com.practice.sprintfour_notetakers.dto.note.NoteCreateRequest;
import com.practice.sprintfour_notetakers.dto.note.NoteResponse;
import com.practice.sprintfour_notetakers.dto.note.NoteUpdateRequest;
import com.practice.sprintfour_notetakers.entity.Note;
import com.practice.sprintfour_notetakers.entity.User;
import com.practice.sprintfour_notetakers.exception.NoteNotFoundException;
import com.practice.sprintfour_notetakers.repository.NoteRepository;
import com.practice.sprintfour_notetakers.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

    public List<NoteResponse> getUserNotes(String userEmail){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Note> notes = noteRepository.findByUserId(user.getId());

        return notes.stream()
                .map(this::mapToNoteResponse)
                .toList();
    }

    public NoteResponse getNoteById(Long noteId, String userEmail){


        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Note note = noteRepository.findByIdAndUserId(noteId, user.getId())
                .orElseThrow(() -> new NoteNotFoundException("Note not found"));

        return mapToNoteResponse(note);
    }

    public NoteResponse updateNote(Long noteId, NoteUpdateRequest request, String userEmail){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Note note = noteRepository.findByIdAndUserId(noteId, user.getId())
                .orElseThrow(() -> new RuntimeException("Note not found"));

        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setUpdatedAt(LocalDateTime.now());

        noteRepository.save(note);

        return mapToNoteResponse(note);
    }

    @Transactional
    public NoteResponse deleteNote(Long noteId, String userEmail){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Note note = noteRepository.findByIdAndUserId(noteId, user.getId())
                .orElseThrow(() -> new RuntimeException("Note not found"));

        user.removeNote(note);
        noteRepository.delete(note);

        return mapToNoteResponse(note);
    }

    private NoteResponse mapToNoteResponse(Note note){
        NoteResponse noteResponse = new NoteResponse();
        noteResponse.setId(note.getId());
        noteResponse.setTitle(note.getTitle());
        noteResponse.setContent(note.getContent());
        noteResponse.setCreatedAt(note.getCreatedAt());
        noteResponse.setUpdatedAt(note.getUpdatedAt());

        return noteResponse;
    }
}
