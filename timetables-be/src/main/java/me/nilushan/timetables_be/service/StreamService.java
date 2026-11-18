package me.nilushan.timetables_be.service;

import me.nilushan.timetables_be.dto.master.StreamRequest;
import me.nilushan.timetables_be.dto.master.StreamResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StreamService {
    StreamResponse create(StreamRequest request);
    StreamResponse update(Long id, StreamRequest request);
    StreamResponse getById(Long id);
    Page<StreamResponse> getAll(Pageable pageable);
    void delete(Long id);
}
