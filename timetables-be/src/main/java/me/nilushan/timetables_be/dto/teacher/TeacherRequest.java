package me.nilushan.timetables_be.dto.teacher;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TeacherRequest(
        @Size(max = 30, message = "Staff number cannot exceed 30 characters")
        String staffNo,

        @NotBlank(message = "Full name is required")
        @Size(max = 150, message = "Full name cannot exceed 150 characters")
        String fullName,

        @Size(max = 10, message = "Short code cannot exceed 10 characters")
        String shortCode,

        @Size(max = 150, message = "Email cannot exceed 150 characters")
        String email,

        @Size(max = 30, message = "Phone cannot exceed 30 characters")
        String phone,

        Short maxPeriodsWeek,
        Short maxPeriodsDay,
        Boolean active
) {}
