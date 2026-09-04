package com.nayibit.lifeguide.common.config;

import com.nayibit.lifeguide.common.security.JwtAuthenticationFilter;
import com.nayibit.lifeguide.common.security.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Filter chain: the security starter alone locks every endpoint behind
 * basic auth, which would block public routes like registration/login.
 * Public endpoints are opened here explicitly, driven by
 * {@link SecurityProperties#publicPaths()} (bound from {@code
 * app.security.public-paths} in {@code application.yaml}) rather than a
 * hardcoded matcher list — adding a new public route is then a config
 * edit, not a Java change. Everything else requires authentication, backed
 * by {@link JwtAuthenticationFilter} (see {@link #jwtAuthenticationFilter})
 * — it populates the security context from a valid {@code Authorization:
 * Bearer <token>} header, ahead of Spring Security's own username/password
 * filter (which this stateless API doesn't use). CSRF is disabled since
 * this is a stateless JSON API, not a browser form/session client.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService) {
        // Not a @Component: Spring Boot would auto-register any @Component
        // Filter as a plain servlet filter (running once) AND Spring
        // Security would add it again via addFilterBefore below (running
        // twice). Declaring it only as a @Bean here, wired in explicitly,
        // keeps it to a single, ordered execution inside the security chain.
        return new JwtAuthenticationFilter(jwtService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                     SecurityErrorHandlers securityErrorHandlers,
                                                     JwtAuthenticationFilter jwtAuthenticationFilter,
                                                     SecurityProperties securityProperties) throws Exception {
        String[] publicPaths = securityProperties.publicPaths().toArray(String[]::new);
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(publicPaths).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(securityErrorHandlers)
                        .accessDeniedHandler(securityErrorHandlers)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
