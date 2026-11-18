package me.nilushan.timetables_be.dto.master;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import me.nilushan.timetables_be.model.enums.RoomType;

public record RoomRequest(
        @NotBlank(message = "Room name is required")
        @Size(max = 60, message = "Room name cannot exceed 60 characters")
        String name,

        @NotNull(message = "Room type is required")
        RoomType type,

        Short capacity
) {}
