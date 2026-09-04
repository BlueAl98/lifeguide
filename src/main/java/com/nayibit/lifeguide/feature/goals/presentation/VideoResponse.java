package com.nayibit.lifeguide.feature.goals.presentation;

import com.nayibit.lifeguide.feature.goals.domain.Video;

public record VideoResponse(
        Long id,
        String name,
        String url
) {
    public static VideoResponse from(Video video) {
        return new VideoResponse(video.getId(), video.getName(), video.getUrl());
    }
}
