package com.practice.sprintfour_notetakers.dto.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SystemStats {
    private long totalUsers;
    private long totalNotes;
    private long notesToday;
}
