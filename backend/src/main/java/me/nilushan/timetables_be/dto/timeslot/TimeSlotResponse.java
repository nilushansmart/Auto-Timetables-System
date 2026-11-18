package me.nilushan.timetables_be.dto.timeslot;

import me.nilushan.timetables_be.model.enums.SlotType;

import java.time.LocalTime;

public record TimeSlotResponse(
        Long id,
        Long schoolId,
        Short dayOfWeek,
        Short periodNo,
        LocalTime startTime,
        LocalTime endTime,
        SlotType slotType
) {}
