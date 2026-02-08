package com.practice.sprintfour_notetakers.controller;

import com.practice.sprintfour_notetakers.dto.note.NoteCreateRequest;
import com.practice.sprintfour_notetakers.dto.note.NoteResponse;
import com.practice.sprintfour_notetakers.dto.note.NoteUpdateRequest;
import com.practice.sprintfour_notetakers.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    public ResponseEntity<NoteResponse> postNote(
            @Valid @RequestBody NoteCreateRequest request,
            Principal principal){
        NoteResponse note = noteService.createNote(request, principal.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(note);
    }

    @GetMapping
    public ResponseEntity<List<NoteResponse>> getUserNotes(Principal principal){
        List<NoteResponse> notes = noteService.getUserNotes(principal.getName());

        return ResponseEntity.ok(notes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> getSpecificNote(
            @PathVariable Long id,
            Principal principal){
        NoteResponse note = noteService.getNoteById(id, principal.getName());

        return ResponseEntity.ok(note);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> putNote(
            @PathVariable Long id,
            @Valid @RequestBody NoteUpdateRequest request,
            Principal principal){
        NoteResponse note = noteService.updateNote(id, request, principal.getName());

        return ResponseEntity.ok(note);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<NoteResponse> deleteNote(
            @PathVariable Long id,
            Principal principal){
        NoteResponse note = noteService.deleteNote(id, principal.getName());

        return ResponseEntity.ok(note);
    }
}
