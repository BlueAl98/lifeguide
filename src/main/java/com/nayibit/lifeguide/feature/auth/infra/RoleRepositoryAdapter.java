package com.nayibit.lifeguide.feature.auth.infra;

import com.nayibit.lifeguide.common.exception.AppException;
import com.nayibit.lifeguide.common.exception.ErrorCode;
import com.nayibit.lifeguide.feature.auth.application.RoleRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleRepositoryAdapter implements RoleRepository {

    private final RoleJpaRepository roleJpaRepository;
    private final UserRoleJpaRepository userRoleJpaRepository;

    public RoleRepositoryAdapter(RoleJpaRepository roleJpaRepository, UserRoleJpaRepository userRoleJpaRepository) {
        this.roleJpaRepository = roleJpaRepository;
        this.userRoleJpaRepository = userRoleJpaRepository;
    }

    @Override
    public void assignRole(Long userId, String roleName) {
        RoleEntity role = roleJpaRepository.findByName(roleName)
                .orElseThrow(() -> new AppException(ErrorCode.INTERNAL_ERROR, "Unknown role: " + roleName));
        userRoleJpaRepository.save(new UserRoleEntity(new UserRoleId(userId, role.getId())));
    }

    @Override
    public List<String> findRoleNames(Long userId) {
       return roleJpaRepository.findRoleNamesByUserId(userId);
    }
}
