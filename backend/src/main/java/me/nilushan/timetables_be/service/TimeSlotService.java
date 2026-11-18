package me.nilushan.timetables_be.service;

import me.nilushan.timetables_be.dto.timeslot.TimeSlotRequest;
import me.nilushan.timetables_be.dto.timeslot.TimeSlotResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TimeSlotService {
    TimeSlotResponse create(TimeSlotRequest request);
    TimeSlotResponse update(Long id, TimeSlotRequest request);
    TimeSlotResponse getById(Long id);
    Page<TimeSlotResponse> getAll(Pageable pageable);
    List<TimeSlotResponse> getAllOrdered();
    List<TimeSlotResponse> getByDayOfWeek(Short dayOfWeek);
    void delete(Long id);
}
