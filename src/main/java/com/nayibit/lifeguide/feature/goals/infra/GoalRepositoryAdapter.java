package com.nayibit.lifeguide.feature.goals.infra;

import com.nayibit.lifeguide.feature.goals.application.GoalRepository;
import com.nayibit.lifeguide.feature.goals.domain.Category;
import com.nayibit.lifeguide.feature.goals.domain.Comment;
import com.nayibit.lifeguide.feature.goals.domain.Foro;
import com.nayibit.lifeguide.feature.goals.domain.Goal;
import com.nayibit.lifeguide.feature.goals.domain.Video;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapts the {@link GoalRepository} application port to Spring Data JPA,
 * translating between the domain {@link Goal}/{@link Category}/{@link Foro}/
 * {@link Video}/{@link Comment} and the persistence {@link GoalEntity}/
 * {@link CategoryEntity}/{@link ForoEntity}/{@link VideoEntity}/
 * {@link CommentEntity}.
 */
@Repository
public class GoalRepositoryAdapter implements GoalRepository {

    private final GoalJpaRepository goalJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final ForoJpaRepository foroJpaRepository;
    private final VideoJpaRepository videoJpaRepository;
    private final CommentJpaRepository commentJpaRepository;

    public GoalRepositoryAdapter(GoalJpaRepository goalJpaRepository,
                                  CategoryJpaRepository categoryJpaRepository,
                                  ForoJpaRepository foroJpaRepository,
                                  VideoJpaRepository videoJpaRepository,
                                  CommentJpaRepository commentJpaRepository) {
        this.goalJpaRepository = goalJpaRepository;
        this.categoryJpaRepository = categoryJpaRepository;
        this.foroJpaRepository = foroJpaRepository;
        this.videoJpaRepository = videoJpaRepository;
        this.commentJpaRepository = commentJpaRepository;
    }

    @Override
    public List<Goal> findAll() {
        List<GoalEntity> goalEntities = goalJpaRepository.findAll();
        if (goalEntities.isEmpty()) {
            return List.of();
        }
        List<Long> goalIds = goalEntities.stream().map(GoalEntity::getId).toList();
        Map<Long, List<Category>> categoriesByGoalId = toDomainCategories(categoryJpaRepository.findByGoalIdIn(goalIds)).stream()
                .collect(Collectors.groupingBy(Category::getGoalId));

        return goalEntities.stream()
                .map(entity -> toDomain(entity, categoriesByGoalId.getOrDefault(entity.getId(), List.of())))
                .toList();
    }

    @Override
    public Optional<Goal> findById(Long id) {
        return goalJpaRepository.findById(id)
                .map(entity -> toDomain(entity, toDomainCategories(categoryJpaRepository.findByGoalId(id))));
    }

    private Goal toDomain(GoalEntity entity, List<Category> categories) {
        return Goal.existing(entity.getId(), entity.getName(), categories);
    }

    /**
     * Maps categories to domain and, in the same pass, batches their foros
     * and videos by category id — same call as the goal→categories batching
     * above: avoid N+1 querying per category.
     */
    private List<Category> toDomainCategories(List<CategoryEntity> categoryEntities) {
        if (categoryEntities.isEmpty()) {
            return List.of();
        }
        List<Long> categoryIds = categoryEntities.stream().map(CategoryEntity::getId).toList();
        Map<Long, List<Foro>> forosByCategoryId = toDomainForos(foroJpaRepository.findByCategoryIdIn(categoryIds)).stream()
                .collect(Collectors.groupingBy(Foro::getCategoryId));
        Map<Long, List<Video>> videosByCategoryId = videoJpaRepository.findByCategoryIdIn(categoryIds).stream()
                .map(this::toDomain)
                .collect(Collectors.groupingBy(Video::getCategoryId));

        return categoryEntities.stream()
                .map(entity -> Category.existing(
                        entity.getId(),
                        entity.getName(),
                        entity.getGoalId(),
                        forosByCategoryId.getOrDefault(entity.getId(), List.of()),
                        videosByCategoryId.getOrDefault(entity.getId(), List.of())))
                .toList();
    }

    /**
     * Maps foros to domain and, in the same pass, batches their comments by
     * foro id — same call as the category→foros/videos batching above: avoid
     * N+1 querying per foro.
     */
    private List<Foro> toDomainForos(List<ForoEntity> foroEntities) {
        if (foroEntities.isEmpty()) {
            return List.of();
        }
        List<Long> foroIds = foroEntities.stream().map(ForoEntity::getId).toList();
        Map<Long, List<Comment>> commentsByForoId = commentJpaRepository.findByForoIdIn(foroIds).stream()
                .map(this::toDomain)
                .collect(Collectors.groupingBy(Comment::getForoId));

        return foroEntities.stream()
                .map(entity -> Foro.existing(
                        entity.getId(),
                        entity.getTitle(),
                        entity.getDescription(),
                        entity.getCategoryId(),
                        commentsByForoId.getOrDefault(entity.getId(), List.of())))
                .toList();
    }

    private Video toDomain(VideoEntity entity) {
        return Video.existing(entity.getId(), entity.getName(), entity.getUrl(), entity.getCategoryId());
    }

    private Comment toDomain(CommentEntity entity) {
        return Comment.existing(
                entity.getId(),
                entity.getUsername(),
                entity.getDate(),
                entity.getDescription(),
                entity.getLikes(),
                entity.getForoId());
    }
}
