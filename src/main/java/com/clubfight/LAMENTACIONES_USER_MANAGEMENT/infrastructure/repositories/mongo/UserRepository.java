package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.repositories.mongo;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.persistence.UserDocument;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.enums.Role;

/**
 * Repositorio para un usuario.
 */
public interface UserRepository extends MongoRepository<UserDocument, String> {
    Optional<UserDocument> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<UserDocument> findByUsernameAndRole(String username, Role role);
    void deleteByRoleAndGuestExpirationBefore(Role role, Instant now);
    List<UserDocument> findByRoleAndGuestExpirationAfter(Role role, Instant now);
    List<UserDocument> findByRoleAndGuestExpirationBefore(Role role, Instant now);
    Optional<UserDocument> findByRefreshToken(String refreshToken);
}