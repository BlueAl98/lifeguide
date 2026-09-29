package com.nayibit.lifeguide.feature.auth.application;

import com.nayibit.lifeguide.common.exception.AppException;
import com.nayibit.lifeguide.common.exception.ErrorCode;
import com.nayibit.lifeguide.feature.auth.domain.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Registers a new user: rejects duplicate email/username, hashes the raw
 * password (never persisted or logged in plaintext), saves, then grants the
 * default {@code USER} role. Every self-registered account starts as a
 * plain user — {@code ADMIN} (or a future {@code PREMIUM}) is granted
 * separately, never through this endpoint.
 *
 * <p>The exists-checks give friendly messages for the common case; the
 * {@code UNIQUE} constraints are what actually stop two concurrent requests
 * (double click) from both succeeding — see {@code UserRepositoryAdapter#save}.
 * One transaction so a user is never left without their role.
 */
@Service
public class RegisterUserUseCase {

    private static final String DEFAULT_ROLE = "USER";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public RegisterUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public User register(String email, String username, String rawPassword,
                         String firstName, String lastName, LocalDate birthDate) {
        if (userRepository.existsByEmail(email)) {
            throw new AppException(ErrorCode.CONFLICT, "Email is already registered");
        }
        if (userRepository.existsByUsername(username)) {
            throw new AppException(ErrorCode.CONFLICT, "Username is already taken");
        }

        String passwordHash = passwordEncoder.encode(rawPassword);
        User user = User.register(email, username, passwordHash,
                firstName.strip(), lastName.strip(), birthDate);
        User saved = userRepository.save(user);
        roleRepository.assignRole(saved.getId(), DEFAULT_ROLE);
        return saved;
    }
}
