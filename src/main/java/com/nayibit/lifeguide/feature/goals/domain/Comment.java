package com.nayibit.lifeguide.feature.goals.domain;

import java.time.LocalDateTime;

/**
 * A comment belonging to a single foro (1:N — a foro has many comments).
 * No Spring, no JPA — just the invariants that make a Comment valid.
 */
public class Comment {

    private final Long id;
    private final String username;
    private final LocalDateTime date;
    private final String description;
    private final int likes;
    private final Long foroId;

    private Comment(Long id, String username, LocalDateTime date, String description, int likes, Long foroId) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username must not be blank");
        }
        if (date == null) {
            throw new IllegalArgumentException("date must not be null");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description must not be blank");
        }
        if (likes < 0) {
            throw new IllegalArgumentException("likes must not be negative");
        }
        if (foroId == null) {
            throw new IllegalArgumentException("foroId must not be null");
        }
        this.id = id;
        this.username = username;
        this.date = date;
        this.description = description;
        this.likes = likes;
        this.foroId = foroId;
    }

    /** Reconstructs a comment coming back from persistence. */
    public static Comment existing(Long id, String username, LocalDateTime date, String description, int likes, Long foroId) {
        return new Comment(id, username, date, description, likes, foroId);
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public int getLikes() {
        return likes;
    }

    public Long getForoId() {
        return foroId;
    }
}
