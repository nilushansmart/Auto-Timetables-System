package me.nilushan.timetables_be.dto.auth;

import me.nilushan.timetables_be.model.enums.UserRole;

public record UserResponse(
        Long id,
        Long schoolId,
        String email,
        String fullName,
        UserRole role
) {}
