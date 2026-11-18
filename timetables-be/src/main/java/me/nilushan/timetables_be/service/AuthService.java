package me.nilushan.timetables_be.service;

import me.nilushan.timetables_be.dto.auth.LoginRequest;
import me.nilushan.timetables_be.dto.auth.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
