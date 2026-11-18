package me.nilushan.timetables_be.dto.academic;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AcademicYearRequest(
        @NotNull(message = "Year is required")
        @Min(value = 2000, message = "Year must be at least 2000")
        @Max(value = 2100, message = "Year must not exceed 2100")
        Short year
) {}
