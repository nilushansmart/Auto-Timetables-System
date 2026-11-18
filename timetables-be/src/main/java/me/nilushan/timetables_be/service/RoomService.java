package me.nilushan.timetables_be.service;

import me.nilushan.timetables_be.dto.master.RoomRequest;
import me.nilushan.timetables_be.dto.master.RoomResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoomService {
    RoomResponse create(RoomRequest request);
    RoomResponse update(Long id, RoomRequest request);
    RoomResponse getById(Long id);
    Page<RoomResponse> getAll(Pageable pageable);
    void delete(Long id);
}
