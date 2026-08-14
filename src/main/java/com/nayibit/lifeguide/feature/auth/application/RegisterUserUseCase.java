package com.nayibit.lifeguide.feature.auth.application;

import com.nayibit.lifeguide.common.exception.AppException;
import com.nayibit.lifeguide.common.exception.ErrorCode;
import com.nayibit.lifeguide.feature.auth.domain.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Registers a new user: rejects duplicate email/username, hashes the raw
 * password (never persisted or logged in plaintext), then saves.
 */
@Service
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String email, String username, String rawPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new AppException(ErrorCode.CONFLICT, "Email is already registered");
        }
        if (userRepository.existsByUsername(username)) {
            throw new AppException(ErrorCode.CONFLICT, "Username is already taken");
        }

        String passwordHash = passwordEncoder.encode(rawPassword);
        User user = User.register(email, username, passwordHash);
        return userRepository.save(user);
    }
}
