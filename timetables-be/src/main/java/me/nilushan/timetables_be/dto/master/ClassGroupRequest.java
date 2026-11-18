package me.nilushan.timetables_be.dto.master;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ClassGroupRequest(
        @NotNull(message = "Academic year ID is required")
        Long academicYearId,

        @NotNull(message = "Grade ID is required")
        Long gradeId,

        @NotNull(message = "Medium ID is required")
        Short mediumId,

        Long streamId,

        @NotBlank(message = "Class name is required")
        @Size(max = 30, message = "Class name cannot exceed 30 characters")
        String name,

        Long classTeacherId,

        Short studentCount
) {}
