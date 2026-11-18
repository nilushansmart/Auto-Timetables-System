package me.nilushan.timetables_be.dto.academic;

import java.time.LocalDate;

public record TermResponse(
        Long id,
        Long schoolId,
        Long academicYearId,
        Short termNo,
        LocalDate startDate,
        LocalDate endDate
) {}
