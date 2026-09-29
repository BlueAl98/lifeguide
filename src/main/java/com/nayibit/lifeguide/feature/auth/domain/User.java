package com.nayibit.lifeguide.feature.auth.domain;

import java.time.Instant;
import java.time.LocalDate;

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
    private final String firstName;
    private final String lastName;
    private final LocalDate birthDate;
    private final UserStatus status;
    private final Instant createdAt;

    private User(Long id, String email, String username, String passwordHash,
                  String firstName, String lastName, LocalDate birthDate,
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
        if (firstName == null) {
            throw new IllegalArgumentException("firstName must not be null");
        }
        if (lastName == null) {
            throw new IllegalArgumentException("lastName must not be null");
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
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.status = status;
        this.createdAt = createdAt;
    }

    /**
     * Creates a brand-new, not-yet-persisted user. {@code passwordHash} must already be hashed.
     * {@code birthDate} is optional.
     */
    public static User register(String email, String username, String passwordHash,
                                String firstName, String lastName, LocalDate birthDate) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("firstName must not be blank");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("lastName must not be blank");
        }
        if (birthDate != null && !birthDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("birthDate must be in the past");
        }
        return new User(null, email, username, passwordHash, firstName, lastName, birthDate,
                UserStatus.ACTIVE, Instant.now());
    }

    /**
     * Reconstructs a user coming back from persistence. Names may be empty for
     * users created before they were collected (backfilled by V8).
     */
    public static User existing(Long id, String email, String username, String passwordHash,
                                 String firstName, String lastName, LocalDate birthDate,
                                 UserStatus status, Instant createdAt) {
        return new User(id, email, username, passwordHash, firstName, lastName, birthDate,
                status, createdAt);
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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public UserStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
