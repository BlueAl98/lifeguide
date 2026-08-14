package com.nayibit.lifeguide.common.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Runs once per request, before Spring Security's authorization check.
 * Reads {@code Authorization: Bearer <token>}; if it's a valid, unexpired
 * JWT, populates the {@link SecurityContextHolder} so
 * {@code authorizeHttpRequests().anyRequest().authenticated()} in
 * {@code SecurityConfig} actually has something to authenticate against.
 *
 * A missing or invalid token is deliberately NOT an error here — this
 * filter just leaves the request unauthenticated and moves on. Whether that
 * matters is decided downstream: a public route doesn't care, and a
 * protected route rejects it with a clean 401 via
 * {@code SecurityErrorHandlers} (same "expected client condition, don't
 * log.error" rule as {@code GlobalExceptionHandler}).
 *
 * No roles/authorities are attached yet — same limitation noted in
 * {@link JwtService}.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith(BEARER_PREFIX)) {
            String token = header.substring(BEARER_PREFIX.length());
            try {
                Long userId = jwtService.extractUserId(token);
                var authentication = new UsernamePasswordAuthenticationToken(userId, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtException | IllegalArgumentException ex) {
                log.debug("Rejected invalid/expired JWT: {}", ex.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
