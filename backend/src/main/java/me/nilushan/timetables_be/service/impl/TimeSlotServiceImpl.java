package me.nilushan.timetables_be.service.impl;

import lombok.RequiredArgsConstructor;
import me.nilushan.timetables_be.dto.timeslot.TimeSlotRequest;
import me.nilushan.timetables_be.dto.timeslot.TimeSlotResponse;
import me.nilushan.timetables_be.exception.BadRequestException;
import me.nilushan.timetables_be.exception.ConflictException;
import me.nilushan.timetables_be.exception.ResourceNotFoundException;
import me.nilushan.timetables_be.exception.UnauthorizedException;
import me.nilushan.timetables_be.model.entity.TimeSlot;
import me.nilushan.timetables_be.repository.TimeSlotRepository;
import me.nilushan.timetables_be.security.TenantContext;
import me.nilushan.timetables_be.service.TimeSlotService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeSlotServiceImpl implements TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;

    private Long getRequiredSchoolId() {
        Long schoolId = TenantContext.getSchoolId();
        if (schoolId == null) {
            throw new UnauthorizedException("Tenant context required: no school ID found in authentication");
        }
        return schoolId;
    }

    private void validateTimes(TimeSlotRequest request) {
        if (!request.endTime().isAfter(request.startTime())) {
            throw new BadRequestException("Time slot end time must be after start time");
        }
    }

    @Override
    @Transactional
    public TimeSlotResponse create(TimeSlotRequest request) {
        Long schoolId = getRequiredSchoolId();
        validateTimes(request);

        if (timeSlotRepository.existsBySchoolIdAndDayOfWeekAndPeriodNo(schoolId, request.dayOfWeek(), request.periodNo())) {
            throw new ConflictException("Time slot for day " + request.dayOfWeek() + " and period " + request.periodNo() + " already exists");
        }

        TimeSlot timeSlot = TimeSlot.builder()
                .schoolId(schoolId)
                .dayOfWeek(request.dayOfWeek())
                .periodNo(request.periodNo())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .slotType(request.slotType())
                .build();

        TimeSlot saved = timeSlotRepository.save(timeSlot);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public TimeSlotResponse update(Long id, TimeSlotRequest request) {
        Long schoolId = getRequiredSchoolId();
        validateTimes(request);

        TimeSlot existing = timeSlotRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("TimeSlot", id));

        if (timeSlotRepository.existsBySchoolIdAndDayOfWeekAndPeriodNoAndIdNot(schoolId, request.dayOfWeek(), request.periodNo(), id)) {
            throw new ConflictException("Time slot for day " + request.dayOfWeek() + " and period " + request.periodNo() + " already exists");
        }

        existing.setDayOfWeek(request.dayOfWeek());
        existing.setPeriodNo(request.periodNo());
        existing.setStartTime(request.startTime());
        existing.setEndTime(request.endTime());
        existing.setSlotType(request.slotType());

        TimeSlot saved = timeSlotRepository.save(existing);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TimeSlotResponse getById(Long id) {
        Long schoolId = getRequiredSchoolId();
        TimeSlot slot = timeSlotRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("TimeSlot", id));
        return mapToResponse(slot);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TimeSlotResponse> getAll(Pageable pageable) {
        Long schoolId = getRequiredSchoolId();
        return timeSlotRepository.findBySchoolId(schoolId, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimeSlotResponse> getAllOrdered() {
        Long schoolId = getRequiredSchoolId();
        return timeSlotRepository.findBySchoolIdOrderByDayOfWeekAscPeriodNoAsc(schoolId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimeSlotResponse> getByDayOfWeek(Short dayOfWeek) {
        Long schoolId = getRequiredSchoolId();
        return timeSlotRepository.findBySchoolIdAndDayOfWeekOrderByPeriodNoAsc(schoolId, dayOfWeek).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Long schoolId = getRequiredSchoolId();
        TimeSlot slot = timeSlotRepository.findBySchoolIdAndId(schoolId, id)
                .orElseThrow(() -> new ResourceNotFoundException("TimeSlot", id));
        timeSlotRepository.delete(slot);
    }

    private TimeSlotResponse mapToResponse(TimeSlot slot) {
        return new TimeSlotResponse(
                slot.getId(),
                slot.getSchoolId(),
                slot.getDayOfWeek(),
                slot.getPeriodNo(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getSlotType()
        );
    }
}
