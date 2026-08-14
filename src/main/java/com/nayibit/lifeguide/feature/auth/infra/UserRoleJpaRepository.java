package com.nayibit.lifeguide.feature.auth.infra;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleJpaRepository extends JpaRepository<UserRoleEntity, UserRoleId> {
}
