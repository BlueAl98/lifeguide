package com.nayibit.lifeguide.feature.goals.infra;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "videos")
public class VideoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String url;

    // Plain column, no @ManyToOne navigation — same call as CategoryEntity's
    // goalId (see feature.auth's UserRoleEntity/RoleEntity precedent).
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    protected VideoEntity() {
        // required by JPA
    }

    public VideoEntity(Long id, String name, String url, Long categoryId) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.categoryId = categoryId;
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
