package com.nayibit.lifeguide.feature.goals.presentation;

import com.nayibit.lifeguide.feature.goals.domain.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String username,
        LocalDateTime date,
        String description,
        int likes
) {
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getUsername(),
                comment.getDate(),
                comment.getDescription(),
                comment.getLikes());
    }
}
