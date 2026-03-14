package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.mappers.UserStatsMapper;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.UserStatsRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.UserStats;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.repositories.UserStatsRepository;

import lombok.RequiredArgsConstructor;

/**
 * Adaptador del repositorio de las estadísticas de un usuario.
 */
@Repository
@RequiredArgsConstructor
public class UserStatsRepositoryAdapter implements UserStatsRepositoryPort {

    private final UserStatsRepository mongoRepo;
    private final UserStatsMapper mapper;

    @Override
    public UserStats save(UserStats stats) {

        var document = mapper.toDocument(stats);
        var saved = mongoRepo.save(document);

        return mapper.toDomain(saved);
    }

    @Override
    public Optional<UserStats> findByUserId(String userId) {

        return mongoRepo.findByUserId(userId)
                .map(mapper::toDomain);
    }
}