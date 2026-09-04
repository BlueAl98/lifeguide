package com.nayibit.lifeguide.feature.goals.application;

import com.nayibit.lifeguide.common.exception.AppException;
import com.nayibit.lifeguide.common.exception.ErrorCode;
import com.nayibit.lifeguide.feature.goals.domain.Goal;
import org.springframework.stereotype.Service;

@Service
public class GetGoalByIdUseCase {

    private final GoalRepository goalRepository;

    public GetGoalByIdUseCase(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public Goal getById(Long id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Goal not found"));
    }
}
