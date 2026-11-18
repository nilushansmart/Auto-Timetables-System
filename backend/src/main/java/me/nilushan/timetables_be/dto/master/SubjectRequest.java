package me.nilushan.timetables_be.dto.master;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubjectRequest(
        @NotBlank(message = "Subject code is required")
        @Size(max = 20, message = "Subject code cannot exceed 20 characters")
        String code,

        @NotBlank(message = "English name is required")
        @Size(max = 100, message = "English name cannot exceed 100 characters")
        String nameEn,

        @Size(max = 100, message = "Sinhala name cannot exceed 100 characters")
        String nameSi,

        @Size(max = 100, message = "Tamil name cannot exceed 100 characters")
        String nameTa,

        Boolean isReligion,
        Boolean isPractical
) {}
