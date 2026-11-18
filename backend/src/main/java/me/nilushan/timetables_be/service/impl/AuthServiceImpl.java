package me.nilushan.timetables_be.service.impl;

import lombok.RequiredArgsConstructor;
import me.nilushan.timetables_be.dto.auth.LoginRequest;
import me.nilushan.timetables_be.dto.auth.LoginResponse;
import me.nilushan.timetables_be.dto.auth.UserResponse;
import me.nilushan.timetables_be.model.entity.AppUser;
import me.nilushan.timetables_be.repository.AppUserRepository;
import me.nilushan.timetables_be.security.CustomUserDetails;
import me.nilushan.timetables_be.security.JwtTokenProvider;
import me.nilushan.timetables_be.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AppUserRepository userRepository;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String token = tokenProvider.generateToken(userDetails);

        userRepository.findById(userDetails.getId()).ifPresent(user -> {
            user.setLastLoginAt(OffsetDateTime.now());
            userRepository.save(user);
        });

        UserResponse userResponse = new UserResponse(
                userDetails.getId(),
                userDetails.getSchoolId(),
                userDetails.getEmail(),
                userDetails.getFullName(),
                userDetails.getRole()
        );

        return new LoginResponse(token, "Bearer", 86400, userResponse);
    }
}
