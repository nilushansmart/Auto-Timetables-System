package me.nilushan.timetables_be.dto.master;

import me.nilushan.timetables_be.model.enums.GradeLevel;

public record GradeResponse(
        Long id,
        Long schoolId,
        Short gradeNo,
        GradeLevel level
) {}
