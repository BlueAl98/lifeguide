package com.nayibit.lifeguide.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Minimal filter chain: the security starter alone locks every endpoint
 * behind basic auth, which would block public routes like registration.
 * Public endpoints are opened here explicitly; everything else still
 * requires authentication until a real auth mechanism (e.g. JWT) replaces
 * this. CSRF is disabled since this is a stateless JSON API, not a
 * browser form/session client.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                     SecurityErrorHandlers securityErrorHandlers) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/register").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(securityErrorHandlers)
                        .accessDeniedHandler(securityErrorHandlers)
                );
        return http.build();
    }
}
