package com.nayibit.lifeguide.feature.goals.presentation;

import com.nayibit.lifeguide.feature.goals.domain.Foro;

public record ForoResponse(
        Long id,
        String title,
        String description
) {
    public static ForoResponse from(Foro foro) {
        return new ForoResponse(foro.getId(), foro.getTitle(), foro.getDescription());
    }
}
