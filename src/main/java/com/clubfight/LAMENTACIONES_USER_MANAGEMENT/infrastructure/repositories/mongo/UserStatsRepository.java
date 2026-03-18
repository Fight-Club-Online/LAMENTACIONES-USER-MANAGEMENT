package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.repositories.mongo;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.persistence.UserStatsDocument;

/**
 * Repositorio para las estadíticas de un usuario
 */
@Repository
public interface UserStatsRepository extends MongoRepository<UserStatsDocument, String> {
    Optional<UserStatsDocument> findByUserId(String userId);

}