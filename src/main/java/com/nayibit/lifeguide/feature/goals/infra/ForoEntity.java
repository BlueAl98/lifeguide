package com.nayibit.lifeguide.feature.goals.infra;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "foros")
public class ForoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    // Plain column, no @ManyToOne navigation — same call as CategoryEntity's
    // goalId (see feature.auth's UserRoleEntity/RoleEntity precedent).
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    protected ForoEntity() {
        // required by JPA
    }

    public ForoEntity(Long id, String title, String description, Long categoryId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
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
}
