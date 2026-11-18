package me.nilushan.timetables_be.dto.teacher;

import java.util.List;

public record TeacherResponse(
        Long id,
        Long schoolId,
        String staffNo,
        String fullName,
        String shortCode,
        String email,
        String phone,
        Short maxPeriodsWeek,
        Short maxPeriodsDay,
        Boolean active,
        List<TeacherSubjectResponse> qualifiedSubjects,
        List<TeacherAvailabilityResponse> availability
) {}
