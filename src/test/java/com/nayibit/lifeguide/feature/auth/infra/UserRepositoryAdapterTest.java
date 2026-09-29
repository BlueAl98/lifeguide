package com.nayibit.lifeguide.feature.auth.infra;

import com.nayibit.lifeguide.common.exception.AppException;
import com.nayibit.lifeguide.common.exception.ErrorCode;
import com.nayibit.lifeguide.feature.auth.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Infra unit test with a mocked {@link UserJpaRepository}: checks that a lost
 * race on the UNIQUE constraints (concurrent/double-click registration) is
 * translated to the same 409 the exists-checks give, instead of a 500.
 */
@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private UserJpaRepository jpaRepository;

    @InjectMocks
    private UserRepositoryAdapter adapter;

    private static User newUser() {
        return User.register("jane@example.com", "jane", "hashed-password", "Jane", "Doe", null);
    }

    private static DataIntegrityViolationException uniqueViolation(String constraint) {
        return new DataIntegrityViolationException("could not execute statement",
                new SQLException("duplicate key value violates unique constraint \"" + constraint + "\""));
    }

    @Test
    void save_translatesDuplicateEmailToConflict() {
        when(jpaRepository.save(any(UserEntity.class))).thenThrow(uniqueViolation("users_email_key"));

        assertThatExceptionOfType(AppException.class)
                .isThrownBy(() -> adapter.save(newUser()))
                .withMessage("Email is already registered")
                .satisfies(ex -> assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.CONFLICT));
    }

    @Test
    void save_translatesDuplicateUsernameToConflict() {
        when(jpaRepository.save(any(UserEntity.class))).thenThrow(uniqueViolation("users_username_key"));

        assertThatExceptionOfType(AppException.class)
                .isThrownBy(() -> adapter.save(newUser()))
                .withMessage("Username is already taken");
    }

    @Test
    void save_rethrowsUnknownIntegrityViolations() {
        DataIntegrityViolationException other = uniqueViolation("some_other_constraint");
        when(jpaRepository.save(any(UserEntity.class))).thenThrow(other);

        assertThatExceptionOfType(DataIntegrityViolationException.class)
                .isThrownBy(() -> adapter.save(newUser()))
                .isSameAs(other);
    }
}
