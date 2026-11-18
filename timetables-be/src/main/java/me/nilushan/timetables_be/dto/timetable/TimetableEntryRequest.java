package me.nilushan.timetables_be.dto.timetable;

import jakarta.validation.constraints.NotNull;

public record TimetableEntryRequest(
        @NotNull(message = "Lesson ID is required")
        Long lessonId,

        @NotNull(message = "Time slot ID is required")
        Long timeSlotId,

        Long roomId,

        Boolean isPinned
) {}
