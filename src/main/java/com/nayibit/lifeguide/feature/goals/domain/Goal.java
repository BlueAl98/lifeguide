package com.nayibit.lifeguide.feature.goals.domain;

import java.util.List;

/**
 * A goal, aggregating the categories that belong to it (1:N). No Spring, no
 * JPA — just the invariants that make a Goal valid.
 */
public class Goal {

    private final Long id;
    private final String name;
    private final List<Category> categories;

    private Goal(Long id, String name, List<Category> categories) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        this.id = id;
        this.name = name;
        this.categories = categories == null ? List.of() : List.copyOf(categories);
    }

    /** Reconstructs a goal coming back from persistence. */
    public static Goal existing(Long id, String name, List<Category> categories) {
        return new Goal(id, name, categories);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Category> getCategories() {
        return categories;
    }
}
