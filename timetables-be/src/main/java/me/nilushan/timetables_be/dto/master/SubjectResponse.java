package me.nilushan.timetables_be.dto.master;

public record SubjectResponse(
        Long id,
        Long schoolId,
        String code,
        String nameEn,
        String nameSi,
        String nameTa,
        Boolean isReligion,
        Boolean isPractical
) {}
