package com.nayibit.lifeguide.feature.auth.presentation;

import com.nayibit.lifeguide.feature.auth.domain.User;

import java.time.Instant;
import java.time.LocalDate;

public record RegisterResponse(
        Long id,
        String email,
        String username,
        String firstName,
        String lastName,
        LocalDate birthDate,
        String status,
        Instant createdAt
) {
    public static RegisterResponse from(User user) {
        return new RegisterResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getBirthDate(),
                user.getStatus().name(),
                user.getCreatedAt()
        );
    }
}
