package com.nayibit.lifeguide.feature.auth.application;

import com.nayibit.lifeguide.feature.auth.domain.User;

/**
 * Outcome of a successful login: the authenticated user plus the issued
 * access token and its lifetime, so the presentation layer can build the
 * response without reaching back into {@link com.nayibit.lifeguide.common.security.JwtService}.
 */
public record LoginResult(User user, String accessToken, long expiresInSeconds) {
}
