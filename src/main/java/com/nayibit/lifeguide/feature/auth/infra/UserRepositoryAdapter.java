package com.nayibit.lifeguide.feature.auth.infra;

import com.nayibit.lifeguide.common.exception.AppException;
import com.nayibit.lifeguide.common.exception.ErrorCode;
import com.nayibit.lifeguide.feature.auth.application.UserRepository;
import com.nayibit.lifeguide.feature.auth.domain.User;
import org.springframework.dao.DataIntegrityViolationException;
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
                user.getFirstName(),
                user.getLastName(),
                user.getBirthDate(),
                user.getStatus(),
                user.getCreatedAt()
        );
        try {
            UserEntity saved = jpaRepository.save(entity);
            return toDomain(saved);
        } catch (DataIntegrityViolationException ex) {
            throw translateUniqueViolation(ex);
        }
    }

    /**
     * A concurrent request (e.g. double click) can pass the exists-checks and
     * then lose the race on the UNIQUE constraint. Map it to the same 409 the
     * exists-checks would have given, using the constraint names from V1.
     */
    private RuntimeException translateUniqueViolation(DataIntegrityViolationException ex) {
        String detail = String.valueOf(ex.getMostSpecificCause().getMessage());
        if (detail.contains("users_email_key")) {
            return new AppException(ErrorCode.CONFLICT, "Email is already registered");
        }
        if (detail.contains("users_username_key")) {
            return new AppException(ErrorCode.CONFLICT, "Username is already taken");
        }
        return ex;
    }

    private User toDomain(UserEntity entity) {
        return User.existing(
                entity.getId(),
                entity.getEmail(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getBirthDate(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
