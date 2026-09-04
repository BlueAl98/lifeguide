package com.nayibit.lifeguide.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Binds {@code app.security.*} from {@code application.yaml}. {@code
 * publicPaths} is the {@code permitAll()} matcher list for
 * {@link SecurityConfig} — externalized so adding a new public route is a
 * config edit, not a Java change/recompile. Everything not listed here
 * defaults to {@code authenticated()} in {@link SecurityConfig}.
 *
 * Same binding mechanism as {@link JwtProperties} — discovered and bound by
 * {@code @ConfigurationPropertiesScan} on {@code LifeguideApplication},
 * deliberately NOT {@code @Component} (see {@link JwtProperties}'s Javadoc
 * for why that fails).
 */
@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(List<String> publicPaths) {

    public SecurityProperties {
        publicPaths = publicPaths == null ? List.of() : List.copyOf(publicPaths);
    }
}
