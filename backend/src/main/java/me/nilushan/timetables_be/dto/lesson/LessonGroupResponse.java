package me.nilushan.timetables_be.dto.lesson;

import me.nilushan.timetables_be.model.enums.GroupType;

public record LessonGroupResponse(
        Long id,
        Long schoolId,
        Long academicYearId,
        GroupType type,
        String name
) {}
