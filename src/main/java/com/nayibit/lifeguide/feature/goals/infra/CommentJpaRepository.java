package com.nayibit.lifeguide.feature.goals.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, Long> {

    List<CommentEntity> findByForoId(Long foroId);

    List<CommentEntity> findByForoIdIn(List<Long> foroIds);
}
