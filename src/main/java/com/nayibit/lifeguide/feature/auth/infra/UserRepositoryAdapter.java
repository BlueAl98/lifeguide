package com.nayibit.lifeguide.feature.auth.infra;

import com.nayibit.lifeguide.feature.auth.application.UserRepository;
import com.nayibit.lifeguide.feature.auth.domain.User;
import org.springframework.stereotype.Repository;

/**
 * Adapts the {@link UserRepository} application port to Spring Data JPA,
 * translating between the domain {@link User} and the persistence
 * {@link UserEntity}.
 */
@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserRepositoryAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public User save(User user) {
        UserEntity entity = new UserEntity(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getPasswordHash(),
                user.getStatus(),
                user.getCreatedAt()
        );
        UserEntity saved = jpaRepository.save(entity);
        return User.existing(
                saved.getId(),
                saved.getEmail(),
                saved.getUsername(),
                saved.getPassword(),
                saved.getStatus(),
                saved.getCreatedAt()
        );
    }
}
