package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.repositories;

import org.springframework.data.repository.CrudRepository;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.RefreshToken;

/**
 * Repositorio para el token de refresco.
 */
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    void deleteByUserId(String userId);
}
