package com.nayibit.lifeguide.feature.auth.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoleJpaRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByName(String name);

    @Query("select r.name from UserRoleEntity ur join RoleEntity r on r.id = ur.id.roleId where ur.id.userId = :userId")
    List<String> findRoleNamesByUserId(@Param("userId") Long userId);
}
