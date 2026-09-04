package com.nayibit.lifeguide.feature.goals.domain;

/**
 * A category belonging to a single goal (1:N — a goal has many categories).
 * No Spring, no JPA — just the invariants that make a Category valid.
 */
public class Category {

    private final Long id;
    private final String name;
    private final Long goalId;

    private Category(Long id, String name, Long goalId) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (goalId == null) {
            throw new IllegalArgumentException("goalId must not be null");
        }
        this.id = id;
        this.name = name;
        this.goalId = goalId;
    }

    /** Reconstructs a category coming back from persistence. */
    public static Category existing(Long id, String name, Long goalId) {
        return new Category(id, name, goalId);
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
