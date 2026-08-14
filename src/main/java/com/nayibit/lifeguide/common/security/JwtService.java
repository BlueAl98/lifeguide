package com.nayibit.lifeguide.common.security;

import com.nayibit.lifeguide.common.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

/**
 * Issues and validates JWT access tokens. A thin wrapper around jjwt, in the
 * same spirit as {@code PasswordEncoderConfig} wrapping BCrypt — a technical
 * capability, not a business rule, so it lives in {@code common} rather than
 * a feature, and features depend on it directly rather than through a port.
 *
 * The token carries a subject (the user id), an email claim, and a
 * {@code roles} claim (the user's role names at the moment of login —
 * see {@code LoginUseCase}). Roles are a snapshot taken at login time: a
 * role change doesn't take effect until the user's next login/token
 * refresh, since there's no refresh token yet to force it sooner.
 */
@Component
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationSeconds;

    public JwtService(JwtProperties properties) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secret()));
        this.expirationSeconds = properties.expirationSeconds();
    }

    public String generateAccessToken(Long userId, String email, List<String> roles) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + expirationSeconds * 1000);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiresAt)
                .signWith(signingKey)
                .compact();
    }

    /**
     * Parses and validates {@code token} (signature + expiration), returning
     * the user id from its subject claim.
     *
     * @throws JwtException            if the token is expired, malformed, or
     *                                  the signature doesn't match
     * @throws IllegalArgumentException if the token is blank or its subject
     *                                  isn't a valid id
     */
    public Long extractUserId(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    /**
     * Same validation as {@link #extractUserId}, returning the {@code roles}
     * claim instead. Never returns null — an absent claim (e.g. a token
     * minted before this claim existed) reads back as an empty list rather
     * than forcing every caller to null-check.
     */
    public List<String> extractRoles(String token) {
        List<?> roles = parseClaims(token).get("roles", List.class);
        return roles == null ? List.of() : roles.stream().map(String::valueOf).toList();
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
