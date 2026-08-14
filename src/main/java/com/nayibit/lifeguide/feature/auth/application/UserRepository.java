package com.nayibit.lifeguide.feature.auth.application;

import com.nayibit.lifeguide.feature.auth.domain.User;

/**
 * Port for user persistence. Implemented by an infra adapter against
 * whatever storage technology is actually used (Postgres via JPA today).
 */
public interface UserRepository {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    User save(User user);
}
