package com.nayibit.lifeguide.feature.auth.application;

import com.nayibit.lifeguide.common.exception.AppException;
import com.nayibit.lifeguide.common.exception.ErrorCode;
import com.nayibit.lifeguide.feature.auth.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Application-layer unit test — {@link UserRepository} and
 * {@link PasswordEncoder} are mocked ports/collaborators, no Spring context
 * and no real database involved.
 */
@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    @Test
    void register_hashesPasswordAndSavesNewUser() {
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("jane")).thenReturn(false);
        when(passwordEncoder.encode("raw-password")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User saved = registerUserUseCase.register("jane@example.com", "jane", "raw-password");

        assertThat(saved.getEmail()).isEqualTo("jane@example.com");
        assertThat(saved.getUsername()).isEqualTo("jane");
        assertThat(saved.getPasswordHash()).isEqualTo("hashed-password");

        verify(passwordEncoder).encode("raw-password");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_rejectsDuplicateEmail() {
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(true);

        assertThatExceptionOfType(AppException.class)
                .isThrownBy(() -> registerUserUseCase.register("jane@example.com", "jane", "raw-password"))
                .satisfies(ex -> assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.CONFLICT));

        verify(userRepository, never()).existsByUsername(eq("jane"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_rejectsDuplicateUsername() {
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("jane")).thenReturn(true);

        assertThatExceptionOfType(AppException.class)
                .isThrownBy(() -> registerUserUseCase.register("jane@example.com", "jane", "raw-password"))
                .satisfies(ex -> assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.CONFLICT));

        verify(userRepository, never()).save(any(User.class));
    }
}
