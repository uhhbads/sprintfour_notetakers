package com.practice.sprintfour_notetakers.controller;

import com.practice.sprintfour_notetakers.dto.note.NoteCreateRequest;
import com.practice.sprintfour_notetakers.dto.note.NoteResponse;
import com.practice.sprintfour_notetakers.dto.note.NoteUpdateRequest;
import com.practice.sprintfour_notetakers.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    private String getCurrentUserEmail(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assert auth != null;
        return auth.getName();
    }

    @PostMapping
    public ResponseEntity<NoteResponse> postNote(
            @RequestBody NoteCreateRequest request){
        NoteResponse note = noteService.createNote(request, getCurrentUserEmail());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(note);
    }

    @GetMapping
    public ResponseEntity<List<NoteResponse>> getUserNotes(){
        List<NoteResponse> notes = noteService.getUserNotes(getCurrentUserEmail());

        return ResponseEntity.ok(notes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> getSpecificNote(
            @PathVariable Long id){
        NoteResponse note = noteService.getNoteById(id, getCurrentUserEmail());

        return ResponseEntity.ok(note);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> putNote(
            @PathVariable Long id,
            @RequestBody NoteUpdateRequest request){
        NoteResponse note = noteService.updateNote(id, request, getCurrentUserEmail());

        return ResponseEntity.ok(note);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<NoteResponse> deleteNote(
            @RequestParam Long id){
        NoteResponse note = noteService.deleteNote(id, getCurrentUserEmail());

        return ResponseEntity.ok(note);
    }
}
