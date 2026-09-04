package com.nayibit.lifeguide.feature.goals.presentation;

import com.nayibit.lifeguide.feature.goals.domain.Foro;

import java.util.List;

public record ForoResponse(
        Long id,
        String title,
        String description,
        List<CommentResponse> comments
) {
    public static ForoResponse from(Foro foro) {
        return new ForoResponse(
                foro.getId(),
                foro.getTitle(),
                foro.getDescription(),
                foro.getComments().stream().map(CommentResponse::from).toList());
    }
}
