package com.nayibit.lifeguide.feature.auth.infra;

import com.nayibit.lifeguide.feature.auth.application.UserRepository;
import com.nayibit.lifeguide.feature.auth.domain.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
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
        return toDomain(saved);
    }

    private User toDomain(UserEntity entity) {
        return User.existing(
                entity.getId(),
                entity.getEmail(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
