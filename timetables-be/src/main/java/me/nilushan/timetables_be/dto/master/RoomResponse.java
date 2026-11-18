package me.nilushan.timetables_be.dto.master;

import me.nilushan.timetables_be.model.enums.RoomType;

public record RoomResponse(
        Long id,
        Long schoolId,
        String name,
        RoomType type,
        Short capacity
) {}
