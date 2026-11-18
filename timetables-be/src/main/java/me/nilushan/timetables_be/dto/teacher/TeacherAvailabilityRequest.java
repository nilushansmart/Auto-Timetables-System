package me.nilushan.timetables_be.dto.teacher;

import jakarta.validation.constraints.NotNull;
import me.nilushan.timetables_be.model.enums.AvailabilityKind;

public record TeacherAvailabilityRequest(
        @NotNull(message = "Time slot ID is required")
        Long timeSlotId,

        @NotNull(message = "Availability kind is required")
        AvailabilityKind kind
) {}
