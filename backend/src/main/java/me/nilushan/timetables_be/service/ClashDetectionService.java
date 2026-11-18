package me.nilushan.timetables_be.service;

import me.nilushan.timetables_be.dto.timetable.ClashCheckResult;

public interface ClashDetectionService {
    void validateNoClash(Long schoolId, Long versionId, Long lessonId, Long timeSlotId, Long roomId, Long excludeEntryId);
    ClashCheckResult checkClash(Long schoolId, Long versionId, Long lessonId, Long timeSlotId, Long roomId, Long excludeEntryId);
}
