package me.nilushan.timetables_be.dto.timetable;

import java.util.List;

public record TimetableEntryResponse(
        Long id,
        Long schoolId,
        Long versionId,
        Long lessonId,
        Long timeSlotId,
        Long roomId,
        Boolean isPinned,
        String subjectCode,
        String subjectNameEn,
        String teacherFullName,
        String teacherShortCode,
        String roomName,
        Short dayOfWeek,
        Short periodNo,
        List<String> classNames
) {}
