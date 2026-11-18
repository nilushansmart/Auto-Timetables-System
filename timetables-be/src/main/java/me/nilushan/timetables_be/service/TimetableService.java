package me.nilushan.timetables_be.service;

import me.nilushan.timetables_be.dto.timetable.TimetableEntryRequest;
import me.nilushan.timetables_be.dto.timetable.TimetableEntryResponse;
import me.nilushan.timetables_be.dto.timetable.TimetableVersionRequest;
import me.nilushan.timetables_be.dto.timetable.TimetableVersionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TimetableService {
    TimetableVersionResponse createVersion(TimetableVersionRequest request);
    TimetableVersionResponse getVersionById(Long id);
    Page<TimetableVersionResponse> getVersions(Pageable pageable);
    List<TimetableVersionResponse> getVersionsByTerm(Long termId);

    TimetableEntryResponse createEntry(Long versionId, TimetableEntryRequest request);
    TimetableEntryResponse updateEntry(Long versionId, Long entryId, TimetableEntryRequest request);
    void deleteEntry(Long versionId, Long entryId);

    List<TimetableEntryResponse> getEntriesByClass(Long versionId, Long classGroupId);
    List<TimetableEntryResponse> getEntriesByTeacher(Long versionId, Long teacherId);
    List<TimetableEntryResponse> getEntriesByRoom(Long versionId, Long roomId);
}
