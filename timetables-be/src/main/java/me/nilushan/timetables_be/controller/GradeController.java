package me.nilushan.timetables_be.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.nilushan.timetables_be.dto.master.GradeRequest;
import me.nilushan.timetables_be.dto.master.GradeResponse;
import me.nilushan.timetables_be.service.GradeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/grades")
@RequiredArgsConstructor
@Tag(name = "Grades", description = "Endpoints for managing grades 1 to 13")
public class GradeController {

    private final GradeService gradeService;

    @PostMapping
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Create a grade")
    public ResponseEntity<GradeResponse> create(@Valid @RequestBody GradeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gradeService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Update a grade")
    public ResponseEntity<GradeResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody GradeRequest request) {
        return ResponseEntity.ok(gradeService.update(id, request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER')")
    @Operation(summary = "Get a grade by ID")
    public ResponseEntity<GradeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(gradeService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER')")
    @Operation(summary = "List grades (paginated)")
    public ResponseEntity<Page<GradeResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(gradeService.getAll(pageable));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER')")
    @Operation(summary = "List all grades as an ordered list")
    public ResponseEntity<List<GradeResponse>> getAllList() {
        return ResponseEntity.ok(gradeService.getAllList());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Delete a grade")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gradeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
