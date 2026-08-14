package com.nayibit.lifeguide.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds {@code app.jwt.*} from {@code application.yaml}. {@code secret} must
 * be a Base64-encoded key at least 256 bits long (HS256 requirement) — see
 * the comment next to the default value in {@code application.yaml} for how
 * to generate one. {@code expirationSeconds} is the access token's lifetime;
 * kept short on purpose — there's no refresh token yet, so this is the only
 * expiry in the system for now.
 *
 * Discovered and bound by {@code @ConfigurationPropertiesScan} on
 * {@code LifeguideApplication} — deliberately NOT {@code @Component}: that
 * would make the plain container try to autowire this record's constructor
 * params as beans instead of binding them from config (see the javadoc on
 * {@code @ConfigurationPropertiesScan} there for the exact failure).
 */
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(String secret, long expirationSeconds) {
}
