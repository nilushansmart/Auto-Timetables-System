package me.nilushan.timetables_be.dto.teacher;

import me.nilushan.timetables_be.model.enums.AvailabilityKind;

public record TeacherAvailabilityResponse(
        Long teacherId,
        Long timeSlotId,
        AvailabilityKind kind,
        Short dayOfWeek,
        Short periodNo
) {}
