package me.nilushan.timetables_be.service.impl;

import lombok.RequiredArgsConstructor;
import me.nilushan.timetables_be.dto.master.RoomRequest;
import me.nilushan.timetables_be.dto.master.RoomResponse;
import me.nilushan.timetables_be.exception.ConflictException;
import me.nilushan.timetables_be.exception.ResourceNotFoundException;
import me.nilushan.timetables_be.exception.UnauthorizedException;
import me.nilushan.timetables_be.model.entity.Room;
import me.nilushan.timetables_be.repository.RoomRepository;
import me.nilushan.timetables_be.security.TenantContext;
import me.nilushan.timetables_be.service.RoomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    private Long getRequiredSchoolId() {
        Long schoolId = TenantContext.getSchoolId();
        if (schoolId == null) {
            throw new UnauthorizedException("Tenant context required: no school ID found in authentication");
        }
        return schoolId;
    }

    @Override
    @Transactional
    public RoomResponse create(RoomRequest request) {
        Long schoolId = getRequiredSchoolId();
        if (roomRepository.existsBySchoolIdAndName(schoolId, request.name())) {
            throw new ConflictException("Room with name '" + request.name() + "' already exists");
        }

        Room room = Room.builder()
                .schoolId(schoolId)
                .name(request.name())
                .type(request.type())
                .capacity(request.capacity())
                .build();

        Room saved = roomRepository.save(room);
        return new RoomResponse(saved.getId(), saved.getSchoolId(), saved.getName(), saved.getType(), saved.getCapacity());
    }

    @Override
    @Transactional
    public RoomResponse update(Long id, RoomRequest request) {
        Long schoolId = getRequiredSchoolId();
        Room existing = roomRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", id));

        if (roomRepository.existsBySchoolIdAndNameAndIdNot(schoolId, request.name(), id)) {
            throw new ConflictException("Room with name '" + request.name() + "' already exists");
        }

        existing.setName(request.name());
        existing.setType(request.type());
        existing.setCapacity(request.capacity());

        Room saved = roomRepository.save(existing);
        return new RoomResponse(saved.getId(), saved.getSchoolId(), saved.getName(), saved.getType(), saved.getCapacity());
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponse getById(Long id) {
        Long schoolId = getRequiredSchoolId();
        Room room = roomRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", id));
        return new RoomResponse(room.getId(), room.getSchoolId(), room.getName(), room.getType(), room.getCapacity());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoomResponse> getAll(Pageable pageable) {
        Long schoolId = getRequiredSchoolId();
        return roomRepository.findBySchoolId(schoolId, pageable)
                .map(r -> new RoomResponse(r.getId(), r.getSchoolId(), r.getName(), r.getType(), r.getCapacity()));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Long schoolId = getRequiredSchoolId();
        Room room = roomRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", id));
        roomRepository.delete(room);
    }
}
