package com.nayibit.lifeguide.feature.goals.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoJpaRepository extends JpaRepository<VideoEntity, Long> {

    List<VideoEntity> findByCategoryId(Long categoryId);

    List<VideoEntity> findByCategoryIdIn(List<Long> categoryIds);
}
