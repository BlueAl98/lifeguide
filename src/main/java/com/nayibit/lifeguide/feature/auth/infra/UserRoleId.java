package com.nayibit.lifeguide.feature.auth.infra;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite key for {@code user_roles} (PK is {@code user_id}+{@code role_id}
 * — see {@code V2__create_roles_and_user_roles.sql}). JPA composite keys
 * require {@code equals}/{@code hashCode} to work correctly.
 */
@Embeddable
public class UserRoleId implements Serializable {

    private Long userId;
    private Long roleId;

    protected UserRoleId() {
        // required by JPA
    }

    public UserRoleId(Long userId, Long roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserRoleId that)) {
            return false;
        }
        return Objects.equals(userId, that.userId) && Objects.equals(roleId, that.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, roleId);
    }
}
