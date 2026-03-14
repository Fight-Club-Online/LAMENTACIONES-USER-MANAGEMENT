package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service;

import org.springframework.stereotype.Service;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.GetUserStatsUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.UserStatsRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.UserStats;

import lombok.RequiredArgsConstructor;

/**
 * Servicio para las estadísticas del usuario.
 */
@Service
@RequiredArgsConstructor
public class UserStatsService implements GetUserStatsUseCase {

    private final UserStatsRepositoryPort repository;

    @Override
    public UserStats getStats(String userId) {
        return repository.findByUserId(userId)
                .orElseThrow();
    }
}