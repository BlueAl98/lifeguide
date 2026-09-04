package com.nayibit.lifeguide.feature.goals.infra;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalJpaRepository extends JpaRepository<GoalEntity, Long> {
}
