package com.nayibit.lifeguide.feature.goals.application;

import com.nayibit.lifeguide.feature.goals.domain.Goal;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllGoalsUseCase {

    private final GoalRepository goalRepository;

    public GetAllGoalsUseCase(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public List<Goal> getAll() {
        return goalRepository.findAll();
    }
}
