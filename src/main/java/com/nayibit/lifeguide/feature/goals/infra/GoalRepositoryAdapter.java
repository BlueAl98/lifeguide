package com.nayibit.lifeguide.feature.goals.infra;

import com.nayibit.lifeguide.feature.goals.application.GoalRepository;
import com.nayibit.lifeguide.feature.goals.domain.Category;
import com.nayibit.lifeguide.feature.goals.domain.Goal;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapts the {@link GoalRepository} application port to Spring Data JPA,
 * translating between the domain {@link Goal}/{@link Category} and the
 * persistence {@link GoalEntity}/{@link CategoryEntity}.
 */
@Repository
public class GoalRepositoryAdapter implements GoalRepository {

    private final GoalJpaRepository goalJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;

    public GoalRepositoryAdapter(GoalJpaRepository goalJpaRepository,
                                  CategoryJpaRepository categoryJpaRepository) {
        this.goalJpaRepository = goalJpaRepository;
        this.categoryJpaRepository = categoryJpaRepository;
    }

    @Override
    public List<Goal> findAll() {
        List<GoalEntity> goalEntities = goalJpaRepository.findAll();
        if (goalEntities.isEmpty()) {
            return List.of();
        }
        List<Long> goalIds = goalEntities.stream().map(GoalEntity::getId).toList();
        Map<Long, List<Category>> categoriesByGoalId = categoryJpaRepository.findByGoalIdIn(goalIds).stream()
                .map(this::toDomain)
                .collect(Collectors.groupingBy(Category::getGoalId));

        return goalEntities.stream()
                .map(entity -> toDomain(entity, categoriesByGoalId.getOrDefault(entity.getId(), List.of())))
                .toList();
    }

    @Override
    public Optional<Goal> findById(Long id) {
        return goalJpaRepository.findById(id)
                .map(entity -> toDomain(entity, categoryJpaRepository.findByGoalId(id).stream()
                        .map(this::toDomain)
                        .toList()));
    }

    private Goal toDomain(GoalEntity entity, List<Category> categories) {
        return Goal.existing(entity.getId(), entity.getName(), categories);
    }

    private Category toDomain(CategoryEntity entity) {
        return Category.existing(entity.getId(), entity.getName(), entity.getGoalId());
    }
}
