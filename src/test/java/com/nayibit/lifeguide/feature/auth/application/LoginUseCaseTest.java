package com.nayibit.lifeguide.feature.auth.application;

import com.nayibit.lifeguide.common.exception.AppException;
import com.nayibit.lifeguide.common.exception.ErrorCode;
import com.nayibit.lifeguide.common.security.JwtService;
import com.nayibit.lifeguide.feature.auth.domain.User;
import com.nayibit.lifeguide.feature.auth.domain.UserStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

/**
 * Application-layer unit test — {@link UserRepository}, {@link PasswordEncoder}
 * and {@link JwtService} are mocked collaborators, no Spring context and no
 * real database involved.
 */
@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private LoginUseCase loginUseCase;

    private User existingUser() {
        return User.existing(1L, "jane@example.com", "jane", "hashed-password",
                UserStatus.ACTIVE, Instant.now());
    }

    @Test
    void login_returnsAccessTokenOnValidCredentials() {
        User user = existingUser();
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("raw-password", "hashed-password")).thenReturn(true);
        when(roleRepository.findRoleNames(1L)).thenReturn(List.of("ADMIN", "USER"));
        when(jwtService.generateAccessToken(1L, "jane@example.com", List.of("ADMIN", "USER"))).thenReturn("signed.jwt.token");
        when(jwtService.getExpirationSeconds()).thenReturn(900L);

        LoginResult result = loginUseCase.login("jane@example.com", "raw-password");

        assertThat(result.user()).isEqualTo(user);
        assertThat(result.accessToken()).isEqualTo("signed.jwt.token");
        assertThat(result.expiresInSeconds()).isEqualTo(900L);
    }

    @Test
    void login_rejectsUnknownEmail() {
        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThatExceptionOfType(AppException.class)
                .isThrownBy(() -> loginUseCase.login("nobody@example.com", "raw-password"))
                .satisfies(ex -> assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED));
    }

    @Test
    void login_rejectsWrongPasswordWithSameErrorAsUnknownEmail() {
        User user = existingUser();
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "hashed-password")).thenReturn(false);

        assertThatExceptionOfType(AppException.class)
                .isThrownBy(() -> loginUseCase.login("jane@example.com", "wrong-password"))
                .satisfies(ex -> {
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED);
                    assertThat(ex.getMessage()).isEqualTo("Invalid email or password");
                });
    }
}
