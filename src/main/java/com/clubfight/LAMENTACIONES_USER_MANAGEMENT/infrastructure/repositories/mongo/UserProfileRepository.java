package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.persistence.UserProfileDocument;

import java.util.Optional;

/**
 * Repositorio para el perfil de un usuario.
 */
public interface UserProfileRepository extends MongoRepository<UserProfileDocument, String> {
    Optional<UserProfileDocument> findByUserId(String userId);
    void deleteByUserId(String userId);

}