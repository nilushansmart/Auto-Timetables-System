package me.nilushan.timetables_be.dto.teacher;

import jakarta.validation.constraints.NotNull;

public record TeacherSubjectRequest(
        @NotNull(message = "Subject ID is required")
        Long subjectId,

        @NotNull(message = "Medium ID is required")
        Short mediumId
) {}
