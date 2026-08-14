package com.nayibit.lifeguide.feature.auth.presentation;

import com.nayibit.lifeguide.feature.auth.application.LoginResult;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds
) {
    public static LoginResponse from(LoginResult result) {
        return new LoginResponse(result.accessToken(), "Bearer", result.expiresInSeconds());
    }
}
