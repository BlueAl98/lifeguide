package com.nayibit.lifeguide.feature.goals.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForoJpaRepository extends JpaRepository<ForoEntity, Long> {

    List<ForoEntity> findByCategoryId(Long categoryId);

    List<ForoEntity> findByCategoryIdIn(List<Long> categoryIds);
}
