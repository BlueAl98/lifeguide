package com.nayibit.lifeguide.feature.goals.domain;

/**
 * A video belonging to a single category (1:N — a category has many
 * videos). No Spring, no JPA — just the invariants that make a Video valid.
 */
public class Video {

    private final Long id;
    private final String name;
    private final String url;
    private final Long categoryId;

    private Video(Long id, String name, String url, Long categoryId) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("url must not be blank");
        }
        if (categoryId == null) {
            throw new IllegalArgumentException("categoryId must not be null");
        }
        this.id = id;
        this.name = name;
        this.url = url;
        this.categoryId = categoryId;
    }

    /** Reconstructs a video coming back from persistence. */
    public static Video existing(Long id, String name, String url, Long categoryId) {
        return new Video(id, name, url, categoryId);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public Long getCategoryId() {
        return categoryId;
    }
}
