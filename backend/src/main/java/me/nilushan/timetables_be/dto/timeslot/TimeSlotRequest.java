package me.nilushan.timetables_be.dto.timeslot;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import me.nilushan.timetables_be.model.enums.SlotType;

import java.time.LocalTime;

public record TimeSlotRequest(
        @NotNull(message = "Day of week is required")
        @Min(value = 1, message = "Day of week must be between 1 (Monday) and 7 (Sunday)")
        @Max(value = 7, message = "Day of week must be between 1 (Monday) and 7 (Sunday)")
        Short dayOfWeek,

        @NotNull(message = "Period number is required")
        @Min(value = 1, message = "Period number must be at least 1")
        Short periodNo,

        @NotNull(message = "Start time is required")
        LocalTime startTime,

        @NotNull(message = "End time is required")
        LocalTime endTime,

        @NotNull(message = "Slot type is required")
        SlotType slotType
) {}
