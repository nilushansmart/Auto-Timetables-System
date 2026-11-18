package me.nilushan.timetables_be.dto.lesson;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import me.nilushan.timetables_be.model.enums.RoomType;

import java.util.List;

public record LessonRequest(
        @NotNull(message = "Academic year ID is required")
        Long academicYearId,

        @NotNull(message = "Subject ID is required")
        Long subjectId,

        @NotNull(message = "Teacher ID is required")
        Long teacherId,

        Long lessonGroupId,

        RoomType requiredRoomType,

        Long fixedRoomId,

        @NotNull(message = "Periods per week is required")
        @Min(value = 1, message = "Periods per week must be at least 1")
        Short periodsPerWeek,

        @NotNull(message = "Block size is required")
        @Min(value = 1, message = "Block size must be between 1 and 3")
        @Max(value = 3, message = "Block size must be between 1 and 3")
        Short blockSize,

        @NotNull(message = "Max per day is required")
        @Min(value = 1, message = "Max per day must be at least 1")
        Short maxPerDay,

        @NotEmpty(message = "At least one class group must be assigned to the lesson")
        List<Long> classGroupIds
) {}
