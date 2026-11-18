package me.nilushan.timetables_be.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.nilushan.timetables_be.dto.master.StreamRequest;
import me.nilushan.timetables_be.dto.master.StreamResponse;
import me.nilushan.timetables_be.service.StreamService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/streams")
@RequiredArgsConstructor
@Tag(name = "Streams", description = "Endpoints for managing A/L streams (Science, Commerce, Arts, Technology)")
public class StreamController {

    private final StreamService streamService;

    @PostMapping
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Create an A/L stream")
    public ResponseEntity<StreamResponse> create(@Valid @RequestBody StreamRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(streamService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Update an A/L stream")
    public ResponseEntity<StreamResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody StreamRequest request) {
        return ResponseEntity.ok(streamService.update(id, request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER')")
    @Operation(summary = "Get an A/L stream by ID")
    public ResponseEntity<StreamResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(streamService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER')")
    @Operation(summary = "List A/L streams (paginated)")
    public ResponseEntity<Page<StreamResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(streamService.getAll(pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Delete an A/L stream")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        streamService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
