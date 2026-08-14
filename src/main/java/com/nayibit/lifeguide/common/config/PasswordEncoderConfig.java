package com.nayibit.lifeguide.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Exposes the app-wide password hasher. Depend on {@link PasswordEncoder}
 * (the interface), not {@link BCryptPasswordEncoder} directly, so the
 * algorithm can change later without touching call sites.
 *
 * BCrypt is a one-way hash, not encryption: there is no decode/decrypt.
 * To check a login attempt, hash it and compare via
 * {@code passwordEncoder.matches(rawPassword, storedHash)} — never store
 * or compare raw passwords.
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
