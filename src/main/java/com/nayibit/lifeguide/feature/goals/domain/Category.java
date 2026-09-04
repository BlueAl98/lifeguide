package com.nayibit.lifeguide.feature.goals.domain;

import java.util.List;

/**
 * A category belonging to a single goal (1:N — a goal has many categories),
 * and in turn owning its own foros and videos (1:N each — a category has
 * many foros, a category has many videos). No Spring, no JPA — just the
 * invariants that make a Category valid.
 */
public class Category {

    private final Long id;
    private final String name;
    private final Long goalId;
    private final List<Foro> foros;
    private final List<Video> videos;

    private Category(Long id, String name, Long goalId, List<Foro> foros, List<Video> videos) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (goalId == null) {
            throw new IllegalArgumentException("goalId must not be null");
        }
        this.id = id;
        this.name = name;
        this.goalId = goalId;
        this.foros = foros == null ? List.of() : List.copyOf(foros);
        this.videos = videos == null ? List.of() : List.copyOf(videos);
    }

    /** Reconstructs a category coming back from persistence. */
    public static Category existing(Long id, String name, Long goalId, List<Foro> foros, List<Video> videos) {
        return new Category(id, name, goalId, foros, videos);
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

    public List<Foro> getForos() {
        return foros;
    }

    public List<Video> getVideos() {
        return videos;
    }
}
