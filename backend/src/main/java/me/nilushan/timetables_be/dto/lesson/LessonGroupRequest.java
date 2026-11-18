package me.nilushan.timetables_be.dto.lesson;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import me.nilushan.timetables_be.model.enums.GroupType;

public record LessonGroupRequest(
        @NotNull(message = "Academic year ID is required")
        Long academicYearId,

        @NotNull(message = "Group type is required")
        GroupType type,

        @NotBlank(message = "Group name is required")
        @Size(max = 100, message = "Group name cannot exceed 100 characters")
        String name
) {}
