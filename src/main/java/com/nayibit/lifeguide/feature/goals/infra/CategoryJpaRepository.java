package com.nayibit.lifeguide.feature.goals.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, Long> {

    List<CategoryEntity> findByGoalId(Long goalId);

    List<CategoryEntity> findByGoalIdIn(List<Long> goalIds);
}
