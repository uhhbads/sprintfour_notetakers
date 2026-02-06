package com.practice.sprintfour_notetakers.dto.note;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteCreateRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String content;
}
