package com.nayibit.lifeguide.feature.auth.domain;

import java.time.Instant;

/**
 * Core user aggregate. No Spring, no JPA — just the invariants that make a
 * User valid. The password is always a hash by the time it reaches here;
 * hashing itself is an application/infrastructure concern, not domain.
 */
public class User {

    private final Long id;
    private final String email;
    private final String username;
    private final String passwordHash;
    private final UserStatus status;
    private final Instant createdAt;

    private User(Long id, String email, String username, String passwordHash,
                  UserStatus status, Instant createdAt) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be blank");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username must not be blank");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("passwordHash must not be blank");
        }
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("createdAt must not be null");
        }
        this.id = id;
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.status = status;
        this.createdAt = createdAt;
    }

    /** Creates a brand-new, not-yet-persisted user. {@code passwordHash} must already be hashed. */
    public static User register(String email, String username, String passwordHash) {
        return new User(null, email, username, passwordHash, UserStatus.ACTIVE, Instant.now());
    }

    /** Reconstructs a user coming back from persistence. */
    public static User existing(Long id, String email, String username, String passwordHash,
                                 UserStatus status, Instant createdAt) {
        return new User(id, email, username, passwordHash, status, createdAt);
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
