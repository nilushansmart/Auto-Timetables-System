package me.nilushan.timetables_be.dto.timetable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TimetableVersionRequest(
        @NotNull(message = "Term ID is required")
        Long termId,

        @NotBlank(message = "Version name is required")
        @Size(max = 100, message = "Version name cannot exceed 100 characters")
        String name
) {}
