package com.nayibit.lifeguide.feature.auth.application;

import com.nayibit.lifeguide.common.exception.AppException;
import com.nayibit.lifeguide.common.exception.ErrorCode;
import com.nayibit.lifeguide.common.security.JwtService;
import com.nayibit.lifeguide.feature.auth.domain.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Authenticates a user by email/password and issues a JWT access token.
 *
 * Deliberately throws the same {@link ErrorCode#UNAUTHORIZED} error for
 * "no such email" and "wrong password" alike — telling a client which one
 * failed would let an attacker use the login endpoint to discover which
 * emails are registered.
 */
@Service
public class LoginUseCase {

    private static final String INVALID_CREDENTIALS = "Invalid email or password";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResult login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED, INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new AppException(ErrorCode.UNAUTHORIZED, INVALID_CREDENTIALS);
        }

        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        return new LoginResult(user, accessToken, jwtService.getExpirationSeconds());
    }
}
