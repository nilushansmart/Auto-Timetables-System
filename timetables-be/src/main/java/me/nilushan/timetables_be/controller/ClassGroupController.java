package me.nilushan.timetables_be.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.nilushan.timetables_be.dto.master.ClassGroupRequest;
import me.nilushan.timetables_be.dto.master.ClassGroupResponse;
import me.nilushan.timetables_be.service.ClassGroupService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/classes")
@RequiredArgsConstructor
@Tag(name = "Classes", description = "Endpoints for managing class groups (e.g. 6A, 12 Science)")
public class ClassGroupController {

    private final ClassGroupService classGroupService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR')")
    @Operation(summary = "Create a class group")
    public ResponseEntity<ClassGroupResponse> create(@Valid @RequestBody ClassGroupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(classGroupService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR')")
    @Operation(summary = "Update a class group")
    public ResponseEntity<ClassGroupResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ClassGroupRequest request) {
        return ResponseEntity.ok(classGroupService.update(id, request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER')")
    @Operation(summary = "Get a class group by ID")
    public ResponseEntity<ClassGroupResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(classGroupService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER')")
    @Operation(summary = "List class groups (paginated, or filtered by academicYearId)")
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) Long academicYearId,
            Pageable pageable) {
        if (academicYearId != null) {
            List<ClassGroupResponse> list = classGroupService.getByAcademicYearId(academicYearId);
            return ResponseEntity.ok(list);
        }
        Page<ClassGroupResponse> page = classGroupService.getAll(pageable);
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Delete a class group")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        classGroupService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
