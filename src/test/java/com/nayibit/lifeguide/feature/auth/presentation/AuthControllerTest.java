package com.nayibit.lifeguide.feature.auth.presentation;

import com.nayibit.lifeguide.common.exception.AppException;
import com.nayibit.lifeguide.common.exception.ErrorCode;
import com.nayibit.lifeguide.feature.auth.application.LoginResult;
import com.nayibit.lifeguide.feature.auth.application.LoginUseCase;
import com.nayibit.lifeguide.feature.auth.application.RegisterUserUseCase;
import com.nayibit.lifeguide.feature.auth.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Presentation-layer slice test — only the web layer is loaded (controller +
 * {@code GlobalExceptionHandler}, which is auto-detected since it's a
 * {@code @RestControllerAdvice}). {@link RegisterUserUseCase} is mocked, so
 * this only checks request/response wiring, validation, and error mapping —
 * not business logic (that's {@code RegisterUserUseCaseTest}) or persistence.
 *
 * Security filters are disabled here (see {@code addFilters = false}) since
 * this test isn't exercising security — {@code /api/register} is
 * {@code permitAll()} anyway. Whether/how to slice-test the security filter
 * chain itself is still open, same as the rest of auth (see SKILL.md).
 */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RegisterUserUseCase registerUserUseCase;

    @MockitoBean
    private LoginUseCase loginUseCase;

    @Test
    void register_returns201WithUserOnSuccess() throws Exception {
        User registered = User.existing(1L, "jane@example.com", "jane", "hashed-password",
                com.nayibit.lifeguide.feature.auth.domain.UserStatus.ACTIVE, java.time.Instant.now());
        when(registerUserUseCase.register("jane@example.com", "jane", "raw-password"))
                .thenReturn(registered);

        RegisterRequest request = new RegisterRequest("jane@example.com", "jane", "raw-password");

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("jane@example.com"))
                .andExpect(jsonPath("$.username").value("jane"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void register_returns400WithValidationErrorOnBlankEmail() throws Exception {
        RegisterRequest request = new RegisterRequest("", "jane", "raw-password");

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.path").value("/api/register"));
    }

    @Test
    void register_returns409WhenUseCaseRejectsDuplicate() throws Exception {
        when(registerUserUseCase.register(anyString(), anyString(), anyString()))
                .thenThrow(new AppException(ErrorCode.CONFLICT, "Email is already registered"));

        RegisterRequest request = new RegisterRequest("jane@example.com", "jane", "raw-password");

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("Email is already registered"));
    }

    @Test
    void register_returns405ForWrongHttpMethod() throws Exception {
        mockMvc.perform(get("/api/register"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.code").value("METHOD_NOT_ALLOWED"));
    }

    @Test
    void login_returns200WithAccessTokenOnSuccess() throws Exception {
        User user = User.existing(1L, "jane@example.com", "jane", "hashed-password",
                com.nayibit.lifeguide.feature.auth.domain.UserStatus.ACTIVE, java.time.Instant.now());
        LoginResult result = new LoginResult(user, "signed.jwt.token", 900L);
        when(loginUseCase.login("jane@example.com", "raw-password")).thenReturn(result);

        LoginRequest request = new LoginRequest("jane@example.com", "raw-password");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("signed.jwt.token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresInSeconds").value(900));
    }

    @Test
    void login_returns400WithValidationErrorOnBlankPassword() throws Exception {
        LoginRequest request = new LoginRequest("jane@example.com", "");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.path").value("/api/login"));
    }

    @Test
    void login_returns401WhenCredentialsAreInvalid() throws Exception {
        when(loginUseCase.login(anyString(), anyString()))
                .thenThrow(new AppException(ErrorCode.UNAUTHORIZED, "Invalid email or password"));

        LoginRequest request = new LoginRequest("jane@example.com", "wrong-password");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }
}
