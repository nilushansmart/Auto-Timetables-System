package me.nilushan.timetables_be.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.nilushan.timetables_be.dto.lesson.LessonRequest;
import me.nilushan.timetables_be.dto.lesson.LessonResponse;
import me.nilushan.timetables_be.service.LessonService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lessons")
@RequiredArgsConstructor
@Tag(name = "Lessons", description = "Endpoints for managing lesson requirements, class assignments, and grouping")
public class LessonController {

    private final LessonService lessonService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR')")
    @Operation(summary = "Create a lesson with class allocations")
    public ResponseEntity<LessonResponse> create(@Valid @RequestBody LessonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR')")
    @Operation(summary = "Update a lesson")
    public ResponseEntity<LessonResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody LessonRequest request) {
        return ResponseEntity.ok(lessonService.update(id, request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER')")
    @Operation(summary = "Get a lesson by ID")
    public ResponseEntity<LessonResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(lessonService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER')")
    @Operation(summary = "List lessons (paginated, or filtered by academicYearId)")
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) Long academicYearId,
            Pageable pageable) {
        if (academicYearId != null) {
            List<LessonResponse> list = lessonService.getByAcademicYearId(academicYearId);
            return ResponseEntity.ok(list);
        }
        Page<LessonResponse> page = lessonService.getAll(pageable);
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Delete a lesson")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lessonService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
