package me.nilushan.timetables_be.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.nilushan.timetables_be.dto.academic.AcademicYearRequest;
import me.nilushan.timetables_be.dto.academic.AcademicYearResponse;
import me.nilushan.timetables_be.dto.academic.TermResponse;
import me.nilushan.timetables_be.service.AcademicYearService;
import me.nilushan.timetables_be.service.TermService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/academic-years")
@RequiredArgsConstructor
@Tag(name = "Academic Years", description = "Endpoints for managing school academic years")
public class AcademicYearController {

    private final AcademicYearService academicYearService;
    private final TermService termService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR')")
    @Operation(summary = "Create an academic year")
    public ResponseEntity<AcademicYearResponse> create(@Valid @RequestBody AcademicYearRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(academicYearService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR')")
    @Operation(summary = "Update an academic year")
    public ResponseEntity<AcademicYearResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AcademicYearRequest request) {
        return ResponseEntity.ok(academicYearService.update(id, request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER')")
    @Operation(summary = "Get an academic year by ID")
    public ResponseEntity<AcademicYearResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(academicYearService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER')")
    @Operation(summary = "List academic years (paginated)")
    public ResponseEntity<Page<AcademicYearResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(academicYearService.getAll(pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Delete an academic year")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        academicYearService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/terms")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER')")
    @Operation(summary = "Get terms belonging to an academic year")
    public ResponseEntity<List<TermResponse>> getTerms(@PathVariable Long id) {
        return ResponseEntity.ok(termService.getByAcademicYearId(id));
    }
}
