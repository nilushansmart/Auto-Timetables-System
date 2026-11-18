package me.nilushan.timetables_be.dto.teacher;

public record TeacherSubjectResponse(
        Long teacherId,
        Long subjectId,
        Short mediumId,
        String subjectCode,
        String subjectNameEn,
        String mediumCode,
        String mediumName
) {}
