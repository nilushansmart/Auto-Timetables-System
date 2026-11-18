package me.nilushan.timetables_be.dto.auth;

public record LoginResponse(
        String token,
        String tokenType,
        long expiresIn,
        UserResponse user
) {}
