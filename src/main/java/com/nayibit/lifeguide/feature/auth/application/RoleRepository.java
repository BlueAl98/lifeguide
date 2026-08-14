package com.nayibit.lifeguide.feature.auth.application;

import java.util.List;

/**
 * Port for assigning and reading a user's roles.
 * {@link RegisterUserUseCase} uses {@link #assignRole} to grant the default
 * role on sign-up; {@link LoginUseCase} uses {@link #findRoleNames} to embed
 * the current roles in the access token it issues.
 */
public interface RoleRepository {

    /**
     * Grants {@code roleName} (e.g. {@code "USER"}, {@code "ADMIN"}) to the
     * given user. {@code roleName} must already exist in the {@code roles}
     * table (seeded by migration) — an unknown name is a server bug, not a
     * client error.
     */
    void assignRole(Long userId, String roleName);

    /**
     * Returns the names of every role currently granted to {@code userId}
     * (e.g. {@code ["USER"]}, {@code ["USER", "ADMIN"]}). Empty, never null,
     * if the user has no roles.
     */
    List<String> findRoleNames(Long userId);
}
