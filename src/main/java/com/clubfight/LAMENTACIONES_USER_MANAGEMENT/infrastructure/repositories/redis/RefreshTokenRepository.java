package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.repositories.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.RefreshToken;

/**
 * Repositorio para el token de refresco.
 */
@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    void deleteByUserId(String userId);
}
