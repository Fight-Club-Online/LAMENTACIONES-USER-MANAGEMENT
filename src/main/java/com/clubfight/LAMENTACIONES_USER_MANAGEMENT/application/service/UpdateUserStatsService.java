package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.UpdateUserStatsUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.UserStatsRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.UserStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Servicio que implementa la lógica para actualizar las estadísticas de un usuario después de una pelea.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateUserStatsService implements UpdateUserStatsUseCase {

    private final UserStatsRepositoryPort repository;

    private static final int POINTS_PER_LEVEL = 100;

    @Override
    public void applyFightResult(String userId, boolean won, boolean draw, int pointsChange) {

        // Si no tiene stats aún, las creamos desde cero (primera pelea)
        UserStats stats = repository.findByUserId(userId)
                .orElseGet(() -> new UserStats(userId, 0, 0, 0, 0, 0, 0, 1, 0));

        stats.setTotalFights(stats.getTotalFights() + 1);
        stats.setPoints(Math.max(0, stats.getPoints() + pointsChange));

        if (draw) {
            stats.setDraws(stats.getDraws() + 1);
            stats.setStreak(0);
        } else if (won) {
            stats.setWins(stats.getWins() + 1);
            stats.setStreak(stats.getStreak() + 1);
        } else {
            stats.setLosses(stats.getLosses() + 1);
            stats.setStreak(0);
        }

        stats.setLevel(calculateLevel(stats.getPoints()));

        repository.save(stats);
        log.info("[STATS] userId={} | won={} draw={} | points={} level={} streak={}",
                userId, won, draw,
                stats.getPoints(), stats.getLevel(), stats.getStreak());
    }

    private int calculateLevel(int points) {
        return Math.max(1, (points / POINTS_PER_LEVEL) + 1);
    }
}