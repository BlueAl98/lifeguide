package com.nayibit.lifeguide.feature.goals.domain;

import java.util.List;

/**
 * A discussion forum belonging to a single category (1:N — a category has
 * many foros), which in turn has many comments (1:N). No Spring, no JPA —
 * just the invariants that make a Foro valid.
 */
public class Foro {

    private final Long id;
    private final String title;
    private final String description;
    private final Long categoryId;
    private final List<Comment> comments;

    private Foro(Long id, String title, String description, Long categoryId, List<Comment> comments) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description must not be blank");
        }
        if (categoryId == null) {
            throw new IllegalArgumentException("categoryId must not be null");
        }
        this.id = id;
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.comments = comments == null ? List.of() : List.copyOf(comments);
    }

    /** Reconstructs a foro coming back from persistence. */
    public static Foro existing(Long id, String title, String description, Long categoryId, List<Comment> comments) {
        return new Foro(id, title, description, categoryId, comments);
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public List<Comment> getComments() {
        return comments;
    }
}
