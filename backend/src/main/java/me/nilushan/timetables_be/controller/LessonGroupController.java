package me.nilushan.timetables_be.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.nilushan.timetables_be.dto.lesson.LessonGroupRequest;
import me.nilushan.timetables_be.dto.lesson.LessonGroupResponse;
import me.nilushan.timetables_be.service.LessonGroupService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lesson-groups")
@RequiredArgsConstructor
@Tag(name = "Lesson Groups", description = "Endpoints for managing parallel lesson groups (religion splits, O/L baskets, electives)")
public class LessonGroupController {

    private final LessonGroupService lessonGroupService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR')")
    @Operation(summary = "Create a parallel lesson group")
    public ResponseEntity<LessonGroupResponse> create(@Valid @RequestBody LessonGroupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonGroupService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR')")
    @Operation(summary = "Update a parallel lesson group")
    public ResponseEntity<LessonGroupResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody LessonGroupRequest request) {
        return ResponseEntity.ok(lessonGroupService.update(id, request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER')")
    @Operation(summary = "Get a lesson group by ID")
    public ResponseEntity<LessonGroupResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(lessonGroupService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER')")
    @Operation(summary = "List lesson groups (paginated, or filtered by academicYearId)")
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) Long academicYearId,
            Pageable pageable) {
        if (academicYearId != null) {
            List<LessonGroupResponse> list = lessonGroupService.getByAcademicYearId(academicYearId);
            return ResponseEntity.ok(list);
        }
        Page<LessonGroupResponse> page = lessonGroupService.getAll(pageable);
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Delete a lesson group")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lessonGroupService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
