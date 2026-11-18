package me.nilushan.timetables_be.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.nilushan.timetables_be.dto.timetable.TimetableEntryRequest;
import me.nilushan.timetables_be.dto.timetable.TimetableEntryResponse;
import me.nilushan.timetables_be.dto.timetable.TimetableVersionRequest;
import me.nilushan.timetables_be.dto.timetable.TimetableVersionResponse;
import me.nilushan.timetables_be.service.TimetableService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/timetable-versions")
@RequiredArgsConstructor
@Tag(name = "Timetables", description = "Endpoints for timetable versions, manual entry editing, clash checks, and grid views")
public class TimetableController {

    private final TimetableService timetableService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR')")
    @Operation(summary = "Create a draft timetable version for a term")
    public ResponseEntity<TimetableVersionResponse> createVersion(@Valid @RequestBody TimetableVersionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(timetableService.createVersion(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER')")
    @Operation(summary = "Get a timetable version by ID")
    public ResponseEntity<TimetableVersionResponse> getVersionById(@PathVariable Long id) {
        return ResponseEntity.ok(timetableService.getVersionById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER')")
    @Operation(summary = "List timetable versions (paginated, or filtered by termId)")
    public ResponseEntity<?> getVersions(
            @RequestParam(required = false) Long termId,
            Pageable pageable) {
        if (termId != null) {
            List<TimetableVersionResponse> list = timetableService.getVersionsByTerm(termId);
            return ResponseEntity.ok(list);
        }
        Page<TimetableVersionResponse> page = timetableService.getVersions(pageable);
        return ResponseEntity.ok(page);
    }

    @PostMapping("/{versionId}/entries")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR')")
    @Operation(summary = "Create a timetable entry with full clash detection (teacher, class, room, availability, slot type)")
    public ResponseEntity<TimetableEntryResponse> createEntry(
            @PathVariable Long versionId,
            @Valid @RequestBody TimetableEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(timetableService.createEntry(versionId, request));
    }

    @PutMapping("/{versionId}/entries/{entryId}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR')")
    @Operation(summary = "Update / move a timetable entry with clash detection")
    public ResponseEntity<TimetableEntryResponse> updateEntry(
            @PathVariable Long versionId,
            @PathVariable Long entryId,
            @Valid @RequestBody TimetableEntryRequest request) {
        return ResponseEntity.ok(timetableService.updateEntry(versionId, entryId, request));
    }

    @DeleteMapping("/{versionId}/entries/{entryId}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR')")
    @Operation(summary = "Delete a timetable entry")
    public ResponseEntity<Void> deleteEntry(
            @PathVariable Long versionId,
            @PathVariable Long entryId) {
        timetableService.deleteEntry(versionId, entryId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{versionId}/entries/by-class/{classGroupId}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER')")
    @Operation(summary = "Get all placed timetable entries for a class in a timetable version")
    public ResponseEntity<List<TimetableEntryResponse>> getEntriesByClass(
            @PathVariable Long versionId,
            @PathVariable Long classGroupId) {
        return ResponseEntity.ok(timetableService.getEntriesByClass(versionId, classGroupId));
    }

    @GetMapping("/{versionId}/entries/by-teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER')")
    @Operation(summary = "Get all placed timetable entries for a teacher in a timetable version")
    public ResponseEntity<List<TimetableEntryResponse>> getEntriesByTeacher(
            @PathVariable Long versionId,
            @PathVariable Long teacherId) {
        return ResponseEntity.ok(timetableService.getEntriesByTeacher(versionId, teacherId));
    }

    @GetMapping("/{versionId}/entries/by-room/{roomId}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER')")
    @Operation(summary = "Get all placed timetable entries for a room in a timetable version")
    public ResponseEntity<List<TimetableEntryResponse>> getEntriesByRoom(
            @PathVariable Long versionId,
            @PathVariable Long roomId) {
        return ResponseEntity.ok(timetableService.getEntriesByRoom(versionId, roomId));
    }
}
