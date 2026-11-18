package me.nilushan.timetables_be.dto.master;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import me.nilushan.timetables_be.model.enums.GradeLevel;

public record GradeRequest(
        @NotNull(message = "Grade number is required")
        @Min(value = 1, message = "Grade number must be between 1 and 13")
        @Max(value = 13, message = "Grade number must be between 1 and 13")
        Short gradeNo,

        @NotNull(message = "Grade level is required")
        GradeLevel level
) {}
