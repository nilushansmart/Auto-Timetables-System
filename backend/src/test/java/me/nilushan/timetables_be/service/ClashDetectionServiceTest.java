package me.nilushan.timetables_be.service;

import me.nilushan.timetables_be.dto.timetable.ClashCheckResult;
import me.nilushan.timetables_be.exception.ConflictException;
import me.nilushan.timetables_be.model.entity.*;
import me.nilushan.timetables_be.model.enums.AvailabilityKind;
import me.nilushan.timetables_be.model.enums.SlotType;
import me.nilushan.timetables_be.repository.*;
import me.nilushan.timetables_be.service.impl.ClashDetectionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClashDetectionServiceTest {

    @Mock
    private TimetableEntryRepository timetableEntryRepository;
    @Mock
    private LessonRepository lessonRepository;
    @Mock
    private LessonClassRepository lessonClassRepository;
    @Mock
    private TimeSlotRepository timeSlotRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private TeacherAvailabilityRepository teacherAvailabilityRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private ClassGroupRepository classGroupRepository;
    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private ClashDetectionServiceImpl clashDetectionService;

    private final Long schoolId = 1L;
    private final Long versionId = 10L;
    private final Long slotId = 20L;
    private final Long lessonId = 30L;
    private final Long teacherId = 40L;
    private final Long roomId = 50L;

    private TimeSlot teachingSlot;
    private Lesson targetLesson;
    private Teacher teacher;

    @BeforeEach
    void setUp() {
        teachingSlot = TimeSlot.builder()
                .id(slotId)
                .schoolId(schoolId)
                .dayOfWeek((short) 1)
                .periodNo((short) 2)
                .startTime(LocalTime.of(8, 30))
                .endTime(LocalTime.of(9, 10))
                .slotType(SlotType.TEACHING)
                .build();

        targetLesson = Lesson.builder()
                .id(lessonId)
                .schoolId(schoolId)
                .teacherId(teacherId)
                .subjectId(101L)
                .periodsPerWeek((short) 4)
                .build();

        teacher = Teacher.builder()
                .id(teacherId)
                .schoolId(schoolId)
                .fullName("Kamal Perera")
                .shortCode("KP")
                .build();
    }

    @Test
    @DisplayName("Reject placement in non-teaching slot (e.g. INTERVAL)")
    void whenSlotIsNotTeaching_thenConflict() {
        TimeSlot intervalSlot = TimeSlot.builder()
                .id(slotId)
                .schoolId(schoolId)
                .dayOfWeek((short) 1)
                .periodNo((short) 4)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(10, 20))
                .slotType(SlotType.INTERVAL)
                .build();

        when(timeSlotRepository.findBySchoolIdAndId(schoolId, slotId)).thenReturn(Optional.of(intervalSlot));

        ClashCheckResult result = clashDetectionService.checkClash(schoolId, versionId, lessonId, slotId, null, null);
        assertThat(result.hasClash()).isTrue();
        assertThat(result.clashType()).isEqualTo("NON_TEACHING_SLOT");

        assertThatThrownBy(() -> clashDetectionService.validateNoClash(schoolId, versionId, lessonId, slotId, null, null))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("only TEACHING slots are allowed");
    }

    @Test
    @DisplayName("Reject placement when teacher is marked UNAVAILABLE in slot")
    void whenTeacherUnavailable_thenConflict() {
        when(timeSlotRepository.findBySchoolIdAndId(schoolId, slotId)).thenReturn(Optional.of(teachingSlot));
        when(lessonRepository.findBySchoolIdAndId(schoolId, lessonId)).thenReturn(Optional.of(targetLesson));
        when(teacherRepository.findBySchoolIdAndId(schoolId, teacherId)).thenReturn(Optional.of(teacher));

        TeacherAvailability availability = TeacherAvailability.builder()
                .id(new TeacherAvailabilityId(teacherId, slotId))
                .kind(AvailabilityKind.UNAVAILABLE)
                .build();
        when(teacherAvailabilityRepository.findByIdTeacherIdAndIdTimeSlotId(teacherId, slotId))
                .thenReturn(Optional.of(availability));

        ClashCheckResult result = clashDetectionService.checkClash(schoolId, versionId, lessonId, slotId, null, null);
        assertThat(result.hasClash()).isTrue();
        assertThat(result.clashType()).isEqualTo("TEACHER_UNAVAILABLE");
    }

    @Test
    @DisplayName("Reject placement when teacher is double-booked in same slot")
    void whenTeacherDoubleBooked_thenConflict() {
        when(timeSlotRepository.findBySchoolIdAndId(schoolId, slotId)).thenReturn(Optional.of(teachingSlot));
        when(lessonRepository.findBySchoolIdAndId(schoolId, lessonId)).thenReturn(Optional.of(targetLesson));
        when(teacherRepository.findBySchoolIdAndId(schoolId, teacherId)).thenReturn(Optional.of(teacher));
        when(teacherAvailabilityRepository.findByIdTeacherIdAndIdTimeSlotId(teacherId, slotId)).thenReturn(Optional.empty());

        TimetableEntry existingEntry = TimetableEntry.builder()
                .id(201L)
                .versionId(versionId)
                .timeSlotId(slotId)
                .lessonId(301L)
                .build();

        Lesson existingLesson = Lesson.builder()
                .id(301L)
                .teacherId(teacherId) // Same teacher!
                .subjectId(102L)
                .build();

        when(timetableEntryRepository.findByVersionIdAndTimeSlotId(versionId, slotId))
                .thenReturn(List.of(existingEntry));
        when(lessonRepository.findById(301L)).thenReturn(Optional.of(existingLesson));

        Subject existingSubject = Subject.builder().id(102L).nameEn("Science").build();
        when(subjectRepository.findById(102L)).thenReturn(Optional.of(existingSubject));

        ClashCheckResult result = clashDetectionService.checkClash(schoolId, versionId, lessonId, slotId, null, null);
        assertThat(result.hasClash()).isTrue();
        assertThat(result.clashType()).isEqualTo("TEACHER_DOUBLE_BOOKING");
        assertThat(result.reason()).contains("Teacher clash: Kamal Perera is already scheduled");
    }

    @Test
    @DisplayName("Reject placement when room is double-booked in same slot")
    void whenRoomDoubleBooked_thenConflict() {
        when(timeSlotRepository.findBySchoolIdAndId(schoolId, slotId)).thenReturn(Optional.of(teachingSlot));
        when(lessonRepository.findBySchoolIdAndId(schoolId, lessonId)).thenReturn(Optional.of(targetLesson));
        when(teacherRepository.findBySchoolIdAndId(schoolId, teacherId)).thenReturn(Optional.of(teacher));
        when(teacherAvailabilityRepository.findByIdTeacherIdAndIdTimeSlotId(teacherId, slotId)).thenReturn(Optional.empty());

        Room room = Room.builder().id(roomId).name("Science Lab 1").build();
        when(roomRepository.findBySchoolIdAndId(schoolId, roomId)).thenReturn(Optional.of(room));

        TimetableEntry existingEntry = TimetableEntry.builder()
                .id(202L)
                .versionId(versionId)
                .timeSlotId(slotId)
                .lessonId(302L)
                .roomId(roomId) // Same room!
                .build();

        Lesson existingLesson = Lesson.builder()
                .id(302L)
                .teacherId(999L) // Different teacher
                .subjectId(103L)
                .build();

        when(timetableEntryRepository.findByVersionIdAndTimeSlotId(versionId, slotId))
                .thenReturn(List.of(existingEntry));
        when(lessonRepository.findById(302L)).thenReturn(Optional.of(existingLesson));

        ClashCheckResult result = clashDetectionService.checkClash(schoolId, versionId, lessonId, slotId, roomId, null);
        assertThat(result.hasClash()).isTrue();
        assertThat(result.clashType()).isEqualTo("ROOM_DOUBLE_BOOKING");
        assertThat(result.reason()).contains("Room clash: Room 'Science Lab 1' is already occupied");
    }

    @Test
    @DisplayName("Reject placement when class is double-booked without same lesson group")
    void whenClassDoubleBooked_withoutSameLessonGroup_thenConflict() {
        when(timeSlotRepository.findBySchoolIdAndId(schoolId, slotId)).thenReturn(Optional.of(teachingSlot));
        when(lessonRepository.findBySchoolIdAndId(schoolId, lessonId)).thenReturn(Optional.of(targetLesson));
        when(teacherRepository.findBySchoolIdAndId(schoolId, teacherId)).thenReturn(Optional.of(teacher));
        when(teacherAvailabilityRepository.findByIdTeacherIdAndIdTimeSlotId(teacherId, slotId)).thenReturn(Optional.empty());

        Long classGroupId = 501L;
        LessonClass targetClass = LessonClass.builder().id(new LessonClassId(lessonId, classGroupId)).build();
        when(lessonClassRepository.findByIdLessonId(lessonId)).thenReturn(List.of(targetClass));

        TimetableEntry existingEntry = TimetableEntry.builder()
                .id(203L)
                .versionId(versionId)
                .timeSlotId(slotId)
                .lessonId(303L)
                .build();

        Lesson existingLesson = Lesson.builder()
                .id(303L)
                .teacherId(888L) // Different teacher
                .subjectId(104L)
                .lessonGroupId(null) // No shared group
                .build();

        when(timetableEntryRepository.findByVersionIdAndTimeSlotId(versionId, slotId))
                .thenReturn(List.of(existingEntry));
        when(lessonRepository.findById(303L)).thenReturn(Optional.of(existingLesson));

        LessonClass existingClass = LessonClass.builder().id(new LessonClassId(303L, classGroupId)).build();
        when(lessonClassRepository.findByIdLessonId(303L)).thenReturn(List.of(existingClass));

        ClassGroup cg = ClassGroup.builder().id(classGroupId).name("10A").build();
        when(classGroupRepository.findById(classGroupId)).thenReturn(Optional.of(cg));

        Subject sub = Subject.builder().id(104L).nameEn("History").build();
        when(subjectRepository.findById(104L)).thenReturn(Optional.of(sub));

        ClashCheckResult result = clashDetectionService.checkClash(schoolId, versionId, lessonId, slotId, null, null);
        assertThat(result.hasClash()).isTrue();
        assertThat(result.clashType()).isEqualTo("CLASS_DOUBLE_BOOKING");
        assertThat(result.reason()).contains("Class clash: 10A is already scheduled for 'History'");
    }

    @Test
    @DisplayName("Permit placement when class is shared but lessons belong to the same parallel lesson_group")
    void whenClassShared_withSameLessonGroup_thenAllowed() {
        Long groupId = 777L;
        targetLesson.setLessonGroupId(groupId);

        when(timeSlotRepository.findBySchoolIdAndId(schoolId, slotId)).thenReturn(Optional.of(teachingSlot));
        when(lessonRepository.findBySchoolIdAndId(schoolId, lessonId)).thenReturn(Optional.of(targetLesson));
        when(teacherRepository.findBySchoolIdAndId(schoolId, teacherId)).thenReturn(Optional.of(teacher));
        when(teacherAvailabilityRepository.findByIdTeacherIdAndIdTimeSlotId(teacherId, slotId)).thenReturn(Optional.empty());

        Long classGroupId = 501L;
        LessonClass targetClass = LessonClass.builder().id(new LessonClassId(lessonId, classGroupId)).build();
        when(lessonClassRepository.findByIdLessonId(lessonId)).thenReturn(List.of(targetClass));

        TimetableEntry existingEntry = TimetableEntry.builder()
                .id(204L)
                .versionId(versionId)
                .timeSlotId(slotId)
                .lessonId(304L)
                .build();

        Lesson existingLesson = Lesson.builder()
                .id(304L)
                .teacherId(777L)
                .subjectId(105L)
                .lessonGroupId(groupId) // Same group (e.g. Religion basket)!
                .build();

        when(timetableEntryRepository.findByVersionIdAndTimeSlotId(versionId, slotId))
                .thenReturn(List.of(existingEntry));
        when(lessonRepository.findById(304L)).thenReturn(Optional.of(existingLesson));

        LessonClass existingClass = LessonClass.builder().id(new LessonClassId(304L, classGroupId)).build();
        when(lessonClassRepository.findByIdLessonId(304L)).thenReturn(List.of(existingClass));

        ClashCheckResult result = clashDetectionService.checkClash(schoolId, versionId, lessonId, slotId, null, null);
        assertThat(result.hasClash()).isFalse();
    }

    @Test
    @DisplayName("Ignore entry itself when updating (excludeEntryId)")
    void whenUpdatingSameEntry_thenExcludeSelf() {
        Long entryId = 205L;

        when(timeSlotRepository.findBySchoolIdAndId(schoolId, slotId)).thenReturn(Optional.of(teachingSlot));
        when(lessonRepository.findBySchoolIdAndId(schoolId, lessonId)).thenReturn(Optional.of(targetLesson));
        when(teacherRepository.findBySchoolIdAndId(schoolId, teacherId)).thenReturn(Optional.of(teacher));
        when(teacherAvailabilityRepository.findByIdTeacherIdAndIdTimeSlotId(teacherId, slotId)).thenReturn(Optional.empty());

        TimetableEntry selfEntry = TimetableEntry.builder()
                .id(entryId)
                .versionId(versionId)
                .timeSlotId(slotId)
                .lessonId(lessonId)
                .build();

        when(timetableEntryRepository.findByVersionIdAndTimeSlotId(versionId, slotId))
                .thenReturn(List.of(selfEntry));
        when(lessonClassRepository.findByIdLessonId(lessonId)).thenReturn(List.of());

        ClashCheckResult result = clashDetectionService.checkClash(schoolId, versionId, lessonId, slotId, null, entryId);
        assertThat(result.hasClash()).isFalse();
    }
}
