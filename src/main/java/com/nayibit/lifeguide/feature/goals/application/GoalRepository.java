package com.nayibit.lifeguide.feature.goals.application;

import com.nayibit.lifeguide.feature.goals.domain.Goal;

import java.util.List;
import java.util.Optional;

/**
 * Port implemented by the infra layer. Read-only for now — only the
 * operations the current use cases need.
 */
public interface GoalRepository {

    List<Goal> findAll();

    Optional<Goal> findById(Long id);
}
