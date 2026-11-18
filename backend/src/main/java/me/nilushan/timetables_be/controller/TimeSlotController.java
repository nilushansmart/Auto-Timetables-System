package me.nilushan.timetables_be.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.nilushan.timetables_be.dto.timeslot.TimeSlotRequest;
import me.nilushan.timetables_be.dto.timeslot.TimeSlotResponse;
import me.nilushan.timetables_be.service.TimeSlotService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/time-slots")
@RequiredArgsConstructor
@Tag(name = "Time Slots", description = "Endpoints for defining weekly period grid")
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR')")
    @Operation(summary = "Create a time slot")
    public ResponseEntity<TimeSlotResponse> create(@Valid @RequestBody TimeSlotRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(timeSlotService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR')")
    @Operation(summary = "Update a time slot")
    public ResponseEntity<TimeSlotResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody TimeSlotRequest request) {
        return ResponseEntity.ok(timeSlotService.update(id, request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER')")
    @Operation(summary = "Get a time slot by ID")
    public ResponseEntity<TimeSlotResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(timeSlotService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER')")
    @Operation(summary = "List all time slots (paginated)")
    public ResponseEntity<Page<TimeSlotResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(timeSlotService.getAll(pageable));
    }

    @GetMapping("/grid")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER')")
    @Operation(summary = "Get all time slots ordered by day and period (full grid)")
    public ResponseEntity<List<TimeSlotResponse>> getGrid() {
        return ResponseEntity.ok(timeSlotService.getAllOrdered());
    }

    @GetMapping("/by-day/{dayOfWeek}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'COORDINATOR', 'TEACHER')")
    @Operation(summary = "Get time slots for a specific day of week (1=Mon ... 7=Sun)")
    public ResponseEntity<List<TimeSlotResponse>> getByDay(@PathVariable Short dayOfWeek) {
        return ResponseEntity.ok(timeSlotService.getByDayOfWeek(dayOfWeek));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Delete a time slot")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        timeSlotService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
