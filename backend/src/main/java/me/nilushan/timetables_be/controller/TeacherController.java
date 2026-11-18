package me.nilushan.timetables_be.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.nilushan.timetables_be.dto.teacher.*;
import me.nilushan.timetables_be.service.TeacherService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
@Tag(name = "Teachers", description = "Endpoints for managing teachers, subject qualifications, and availability")
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Create a teacher")
    public ResponseEntity<TeacherResponse> create(@Valid @RequestBody TeacherRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teacherService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Update a teacher")
    public ResponseEntity<TeacherResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody TeacherRequest request) {
        return ResponseEntity.ok(teacherService.update(id, request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER')")
    @Operation(summary = "Get a teacher by ID with subject qualifications and availability")
    public ResponseEntity<TeacherResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(teacherService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER')")
    @Operation(summary = "List teachers (paginated)")
    public ResponseEntity<Page<TeacherResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(teacherService.getAll(pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Delete a teacher")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        teacherService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/subjects")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Assign a subject and medium qualification to a teacher")
    public ResponseEntity<TeacherSubjectResponse> addSubject(
            @PathVariable Long id,
            @Valid @RequestBody TeacherSubjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teacherService.addSubject(id, request));
    }

    @DeleteMapping("/{id}/subjects/{subjectId}/mediums/{mediumId}")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Remove a subject qualification from a teacher")
    public ResponseEntity<Void> removeSubject(
            @PathVariable Long id,
            @PathVariable Long subjectId,
            @PathVariable Short mediumId) {
        teacherService.removeSubject(id, subjectId, mediumId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/availability")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER')")
    @Operation(summary = "Set availability for a teacher on a time slot (UNAVAILABLE / PREFERRED)")
    public ResponseEntity<TeacherAvailabilityResponse> setAvailability(
            @PathVariable Long id,
            @Valid @RequestBody TeacherAvailabilityRequest request) {
        return ResponseEntity.ok(teacherService.setAvailability(id, request));
    }

    @DeleteMapping("/{id}/availability/{timeSlotId}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER')")
    @Operation(summary = "Remove an availability constraint for a teacher on a time slot")
    public ResponseEntity<Void> removeAvailability(
            @PathVariable Long id,
            @PathVariable Long timeSlotId) {
        teacherService.removeAvailability(id, timeSlotId);
        return ResponseEntity.noContent().build();
    }
}
