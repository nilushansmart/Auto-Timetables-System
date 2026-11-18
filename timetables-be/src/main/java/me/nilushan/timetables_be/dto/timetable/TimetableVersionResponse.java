package me.nilushan.timetables_be.dto.timetable;

import me.nilushan.timetables_be.model.enums.VersionStatus;

import java.time.OffsetDateTime;

public record TimetableVersionResponse(
        Long id,
        Long schoolId,
        Long termId,
        Long solveJobId,
        String name,
        VersionStatus status,
        Integer hardScore,
        Integer softScore,
        Long createdBy,
        OffsetDateTime createdAt,
        OffsetDateTime publishedAt
) {}
