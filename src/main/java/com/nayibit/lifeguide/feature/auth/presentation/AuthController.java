package com.nayibit.lifeguide.feature.auth.presentation;

import com.nayibit.lifeguide.feature.auth.application.LoginResult;
import com.nayibit.lifeguide.feature.auth.application.LoginUseCase;
import com.nayibit.lifeguide.feature.auth.application.RegisterUserUseCase;
import com.nayibit.lifeguide.feature.auth.domain.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase, LoginUseCase loginUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = registerUserUseCase.register(request.email(), request.username(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(RegisterResponse.from(user));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResult result = loginUseCase.login(request.email(), request.password());
        return ResponseEntity.ok(LoginResponse.from(result));
    }
}
