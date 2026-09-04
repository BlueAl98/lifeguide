package com.nayibit.lifeguide.feature.goals.infra;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int likes;

    // Plain column, no @ManyToOne navigation — same call as CategoryEntity's
    // goalId (see feature.auth's UserRoleEntity/RoleEntity precedent).
    @Column(name = "foro_id", nullable = false)
    private Long foroId;

    protected CommentEntity() {
        // required by JPA
    }

    public CommentEntity(Long id, String username, LocalDateTime date, String description, int likes, Long foroId) {
        this.id = id;
        this.username = username;
        this.date = date;
        this.description = description;
        this.likes = likes;
        this.foroId = foroId;
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
