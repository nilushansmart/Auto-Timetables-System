package me.nilushan.timetables_be.dto.master;

public record ClassGroupResponse(
        Long id,
        Long schoolId,
        Long academicYearId,
        Long gradeId,
        Short mediumId,
        Long streamId,
        String name,
        Long classTeacherId,
        Short studentCount
) {}
