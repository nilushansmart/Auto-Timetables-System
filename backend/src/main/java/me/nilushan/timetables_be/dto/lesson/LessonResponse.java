package me.nilushan.timetables_be.dto.lesson;

import me.nilushan.timetables_be.model.enums.RoomType;

import java.util.List;

public record LessonResponse(
        Long id,
        Long schoolId,
        Long academicYearId,
        Long subjectId,
        Long teacherId,
        Long lessonGroupId,
        RoomType requiredRoomType,
        Long fixedRoomId,
        Short periodsPerWeek,
        Short blockSize,
        Short maxPerDay,
        List<Long> classGroupIds,
        String subjectCode,
        String subjectNameEn,
        String teacherFullName,
        String teacherShortCode,
        String fixedRoomName,
        String lessonGroupName
) {}
