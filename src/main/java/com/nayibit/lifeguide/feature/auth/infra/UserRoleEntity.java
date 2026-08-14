package com.nayibit.lifeguide.feature.auth.infra;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Maps a single {@code user_roles} row — the many-to-many link between a
 * user and a role. Deliberately just the join, no {@code @ManyToOne} back to
 * {@link UserEntity}/{@link RoleEntity}: nothing needs to navigate that
 * relationship as an object graph yet, only insert/query rows by id.
 */
@Entity
@Table(name = "user_roles")
public class UserRoleEntity {

    @EmbeddedId
    private UserRoleId id;

    protected UserRoleEntity() {
        // required by JPA
    }

    public UserRoleEntity(UserRoleId id) {
        this.id = id;
    }

    public UserRoleId getId() {
        return id;
    }
}
