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

/**
 * Issues and validates JWT access tokens. A thin wrapper around jjwt, in the
 * same spirit as {@code PasswordEncoderConfig} wrapping BCrypt — a technical
 * capability, not a business rule, so it lives in {@code common} rather than
 * a feature, and features depend on it directly rather than through a port.
 *
 * The token only carries a subject (the user id) and an email claim — no
 * roles yet, since nothing in the domain model tracks roles today.
 */
@Component
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationSeconds;

    public JwtService(JwtProperties properties) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secret()));
        this.expirationSeconds = properties.expirationSeconds();
    }

    public String generateAccessToken(Long userId, String email) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + expirationSeconds * 1000);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
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
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.valueOf(claims.getSubject());
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }
}
