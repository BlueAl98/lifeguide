package com.nayibit.lifeguide.feature.goals.presentation;

import com.nayibit.lifeguide.feature.goals.domain.Category;

import java.util.List;

public record CategoryResponse(
        Long id,
        String name,
        List<ForoResponse> foros,
        List<VideoResponse> videos
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getForos().stream().map(ForoResponse::from).toList(),
                category.getVideos().stream().map(VideoResponse::from).toList()
        );
    }
}
