package com.nayibit.lifeguide.feature.goals.presentation;

import com.nayibit.lifeguide.feature.goals.domain.Goal;

import java.util.List;

public record GoalResponse(
        Long id,
        String name,
        List<CategoryResponse> categories
) {
    public static GoalResponse from(Goal goal) {
        return new GoalResponse(
                goal.getId(),
                goal.getName(),
                goal.getCategories().stream().map(CategoryResponse::from).toList()
        );
    }
}
