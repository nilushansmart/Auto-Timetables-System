package me.nilushan.timetables_be.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.nilushan.timetables_be.dto.academic.TermRequest;
import me.nilushan.timetables_be.dto.academic.TermResponse;
import me.nilushan.timetables_be.service.TermService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/terms")
@RequiredArgsConstructor
@Tag(name = "Terms", description = "Endpoints for managing school terms")
public class TermController {

    private final TermService termService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR')")
    @Operation(summary = "Create a term")
    public ResponseEntity<TermResponse> create(@Valid @RequestBody TermRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(termService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR')")
    @Operation(summary = "Update a term")
    public ResponseEntity<TermResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody TermRequest request) {
        return ResponseEntity.ok(termService.update(id, request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER')")
    @Operation(summary = "Get a term by ID")
    public ResponseEntity<TermResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(termService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER')")
    @Operation(summary = "List terms (paginated)")
    public ResponseEntity<Page<TermResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(termService.getAll(pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Delete a term")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        termService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
