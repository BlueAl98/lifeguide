package com.nayibit.lifeguide.feature.auth.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * Pure domain unit test — no Spring context, no mocks. {@link User} has no
 * framework dependencies, so it's tested exactly like a plain Java object.
 */
class UserTest {

    @Test
    void register_createsActiveUserWithNoIdYet() {
        User user = User.register("jane@example.com", "jane", "hashed-password",
                "Jane", "Doe", LocalDate.of(1995, 4, 12));

        assertThat(user.getId()).isNull();
        assertThat(user.getEmail()).isEqualTo("jane@example.com");
        assertThat(user.getUsername()).isEqualTo("jane");
        assertThat(user.getPasswordHash()).isEqualTo("hashed-password");
        assertThat(user.getFirstName()).isEqualTo("Jane");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getBirthDate()).isEqualTo(LocalDate.of(1995, 4, 12));
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getCreatedAt()).isNotNull();
    }

    @Test
    void existing_reconstructsUserFromPersistedFields() {
        Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");

        User user = User.existing(1L, "jane@example.com", "jane", "hashed-password",
                "Jane", "Doe", null,
                UserStatus.INACTIVE, createdAt);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getStatus()).isEqualTo(UserStatus.INACTIVE);
        assertThat(user.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void register_rejectsBlankEmail() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> User.register(" ", "jane", "hashed-password", "Jane", "Doe", null))
                .withMessageContaining("email");
    }

    @Test
    void register_rejectsBlankUsername() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> User.register("jane@example.com", " ", "hashed-password", "Jane", "Doe", null))
                .withMessageContaining("username");
    }

    @Test
    void register_rejectsBlankPasswordHash() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> User.register("jane@example.com", "jane", " ", "Jane", "Doe", null))
                .withMessageContaining("passwordHash");
    }

    @Test
    void register_rejectsBlankFirstName() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> User.register("jane@example.com", "jane", "hashed-password", " ", "Doe", null))
                .withMessageContaining("firstName");
    }

    @Test
    void register_rejectsBlankLastName() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> User.register("jane@example.com", "jane", "hashed-password", "Jane", " ", null))
                .withMessageContaining("lastName");
    }

    @Test
    void register_rejectsFutureBirthDate() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> User.register("jane@example.com", "jane", "hashed-password", "Jane", "Doe",
                        LocalDate.now().plusDays(1)))
                .withMessageContaining("birthDate");
    }

    @Test
    void existing_rejectsNullStatus() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> User.existing(1L, "jane@example.com", "jane", "hashed-password",
                "Jane", "Doe", null,
                        null, Instant.now()))
                .withMessageContaining("status");
    }

    @Test
    void existing_rejectsNullCreatedAt() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> User.existing(1L, "jane@example.com", "jane", "hashed-password",
                "Jane", "Doe", null,
                        UserStatus.ACTIVE, null))
                .withMessageContaining("createdAt");
    }
}
