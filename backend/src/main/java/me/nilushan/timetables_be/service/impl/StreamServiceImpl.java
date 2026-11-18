package me.nilushan.timetables_be.service.impl;

import lombok.RequiredArgsConstructor;
import me.nilushan.timetables_be.dto.master.StreamRequest;
import me.nilushan.timetables_be.dto.master.StreamResponse;
import me.nilushan.timetables_be.exception.ConflictException;
import me.nilushan.timetables_be.exception.ResourceNotFoundException;
import me.nilushan.timetables_be.exception.UnauthorizedException;
import me.nilushan.timetables_be.model.entity.Stream;
import me.nilushan.timetables_be.repository.StreamRepository;
import me.nilushan.timetables_be.security.TenantContext;
import me.nilushan.timetables_be.service.StreamService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StreamServiceImpl implements StreamService {

    private final StreamRepository streamRepository;

    private Long getRequiredSchoolId() {
        Long schoolId = TenantContext.getSchoolId();
        if (schoolId == null) {
            throw new UnauthorizedException("Tenant context required: no school ID found in authentication");
        }
        return schoolId;
    }

    @Override
    @Transactional
    public StreamResponse create(StreamRequest request) {
        Long schoolId = getRequiredSchoolId();
        if (streamRepository.existsBySchoolIdAndName(schoolId, request.name())) {
            throw new ConflictException("Stream with name '" + request.name() + "' already exists");
        }

        Stream stream = Stream.builder()
                .schoolId(schoolId)
                .name(request.name())
                .build();

        Stream saved = streamRepository.save(stream);
        return new StreamResponse(saved.getId(), saved.getSchoolId(), saved.getName());
    }

    @Override
    @Transactional
    public StreamResponse update(Long id, StreamRequest request) {
        Long schoolId = getRequiredSchoolId();
        Stream existing = streamRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Stream", id));

        if (streamRepository.existsBySchoolIdAndNameAndIdNot(schoolId, request.name(), id)) {
            throw new ConflictException("Stream with name '" + request.name() + "' already exists");
        }

        existing.setName(request.name());
        Stream saved = streamRepository.save(existing);
        return new StreamResponse(saved.getId(), saved.getSchoolId(), saved.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public StreamResponse getById(Long id) {
        Long schoolId = getRequiredSchoolId();
        Stream stream = streamRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Stream", id));
        return new StreamResponse(stream.getId(), stream.getSchoolId(), stream.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StreamResponse> getAll(Pageable pageable) {
        Long schoolId = getRequiredSchoolId();
        return streamRepository.findBySchoolId(schoolId, pageable)
                .map(s -> new StreamResponse(s.getId(), s.getSchoolId(), s.getName()));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Long schoolId = getRequiredSchoolId();
        Stream stream = streamRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Stream", id));
        streamRepository.delete(stream);
    }
}
