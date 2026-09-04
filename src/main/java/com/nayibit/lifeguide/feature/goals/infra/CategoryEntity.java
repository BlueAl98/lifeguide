package com.nayibit.lifeguide.feature.goals.infra;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "categories")
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Plain column, no @ManyToOne navigation — same call as
    // feature.auth's UserRoleEntity/RoleEntity: the join is spelled out at
    // the query site (see CategoryJpaRepository) rather than walked via a
    // mapped association.
    @Column(name = "goal_id", nullable = false)
    private Long goalId;

    protected CategoryEntity() {
        // required by JPA
    }

    public CategoryEntity(Long id, String name, Long goalId) {
        this.id = id;
        this.name = name;
        this.goalId = goalId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getGoalId() {
        return goalId;
    }
}
