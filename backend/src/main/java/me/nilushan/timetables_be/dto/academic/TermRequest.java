package me.nilushan.timetables_be.dto.academic;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TermRequest(
        @NotNull(message = "Academic year ID is required")
        Long academicYearId,

        @NotNull(message = "Term number is required")
        @Min(value = 1, message = "Term number must be between 1 and 3")
        @Max(value = 3, message = "Term number must be between 1 and 3")
        Short termNo,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        @NotNull(message = "End date is required")
        LocalDate endDate
) {}
