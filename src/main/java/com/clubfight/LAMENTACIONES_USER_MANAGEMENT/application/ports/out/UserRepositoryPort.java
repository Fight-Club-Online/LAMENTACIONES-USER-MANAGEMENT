package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.User;

/**
 * Puerto de salida para la gestión de usuarios.
 */
public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(String userId);
    boolean existsByEmail(String email);

    Optional<User> findGuestByUsername(String username);
    List<User> findActiveGuests(Instant now);

    Optional<User> findByRefreshToken(String refreshToken);
    List<User> findExpiredGuests(Instant now);
    void delete(User user);

}
