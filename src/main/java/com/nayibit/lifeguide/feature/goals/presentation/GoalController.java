package com.nayibit.lifeguide.feature.goals.presentation;

import com.nayibit.lifeguide.feature.goals.application.GetAllGoalsUseCase;
import com.nayibit.lifeguide.feature.goals.application.GetGoalByIdUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GetAllGoalsUseCase getAllGoalsUseCase;
    private final GetGoalByIdUseCase getGoalByIdUseCase;

    public GoalController(GetAllGoalsUseCase getAllGoalsUseCase, GetGoalByIdUseCase getGoalByIdUseCase) {
        this.getAllGoalsUseCase = getAllGoalsUseCase;
        this.getGoalByIdUseCase = getGoalByIdUseCase;
    }

    @GetMapping
    public ResponseEntity<List<GoalResponse>> getAll() {
        List<GoalResponse> response = getAllGoalsUseCase.getAll().stream()
                .map(GoalResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(GoalResponse.from(getGoalByIdUseCase.getById(id)));
    }
}
