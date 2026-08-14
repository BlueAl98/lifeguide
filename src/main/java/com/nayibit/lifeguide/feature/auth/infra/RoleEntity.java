package com.nayibit.lifeguide.feature.auth.infra;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Maps the {@code roles} table (seeded by {@code V3__seed_roles.sql}:
 * {@code ADMIN}, {@code USER}). No domain type yet — roles aren't part of
 * any business rule beyond "a user has some" today, so this stays an infra
 * concern until that changes.
 */
@Entity
@Table(name = "roles")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    protected RoleEntity() {
        // required by JPA
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
