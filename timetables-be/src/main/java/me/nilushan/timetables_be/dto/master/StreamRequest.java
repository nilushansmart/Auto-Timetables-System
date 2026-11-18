package me.nilushan.timetables_be.dto.master;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StreamRequest(
        @NotBlank(message = "Stream name is required")
        @Size(max = 50, message = "Stream name cannot exceed 50 characters")
        String name
) {}
