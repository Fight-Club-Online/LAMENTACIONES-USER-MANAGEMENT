package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.enums.Achievement;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.enums.Rank;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.UpdateUserStatsUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.UserStatsRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.UserStats;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

/**
 * Servicio que implementa la lógica para actualizar las estadísticas de un usuario después de una pelea.
 * Incluye telemetría directa de negocio para el análisis de comportamiento competitivo.
 */
@Slf4j
@Service
public class UpdateUserStatsService implements UpdateUserStatsUseCase {

    private final UserStatsRepositoryPort repository;
    private final MeterRegistry meterRegistry; 

    private static final int POINTS_PER_LEVEL = 100;

    public UpdateUserStatsService(UserStatsRepositoryPort repository, MeterRegistry meterRegistry) {
        this.repository = repository;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void applyFightResult(String userId, boolean won, boolean draw, int pointsChange) {

        UserStats stats = repository.findByUserId(userId)
                .orElseGet(() -> new UserStats(userId, 0, 0, 0, 0, 0, 0, 1, 0, Rank.HIERRO_I, new HashSet<>()));

        stats.setTotalFights(stats.getTotalFights() + 1);
        stats.setPoints(Math.max(0, stats.getPoints() + pointsChange));

        String resultadoMétrica;
        if (draw) {
            stats.setDraws(stats.getDraws() + 1);
            stats.setStreak(0);
            resultadoMétrica = "empate";
        } else if (won) {
            stats.setWins(stats.getWins() + 1);
            stats.setStreak(stats.getStreak() + 1);
            resultadoMétrica = "victoria";
        } else {
            stats.setLosses(stats.getLosses() + 1);
            stats.setStreak(0);
            resultadoMétrica = "derrota";
        }

        stats.setLevel(calculateLevel(stats.getPoints()));
        stats.setRank(Rank.fromPoints(stats.getPoints()));         
        evaluateAchievements(stats);

        repository.save(stats);

        Counter.builder("peleas_procesadas_total")
                .description("Total de peleas procesadas en la plataforma")
                .tag("resultado", resultadoMétrica)
                .tag("rango", stats.getRank().name())
                .register(meterRegistry)
                .increment();

        log.info("[STATS] userId={} | won={} draw={} | points={} level={} rank={} streak={} achievements={}",
                userId, won, draw,
                stats.getPoints(), stats.getLevel(), stats.getRank(),
                stats.getStreak(), stats.getAchievements());
    }

    private void evaluateAchievements(UserStats stats) {
        Set<Achievement> unlocked = stats.getAchievements();
        if (unlocked == null) {
            unlocked = new HashSet<>();
            stats.setAchievements(unlocked);
        }

        checkAndTrackAchievement(unlocked, stats.getWins() >= 1, Achievement.PRIMERA_SANGRE);
        checkAndTrackAchievement(unlocked, stats.getTotalFights() >= 50, Achievement.VETERANO);
        checkAndTrackAchievement(unlocked, stats.getWins() >= 100, Achievement.LEYENDA);
        checkAndTrackAchievement(unlocked, stats.getStreak() >= 5, Achievement.RACHA_DE_5);
        checkAndTrackAchievement(unlocked, stats.getStreak() >= 10, Achievement.RACHA_DE_10);
        checkAndTrackAchievement(unlocked, stats.getRank() == Rank.PLATINO, Achievement.MAESTRO_DEL_RING);
    }

    private void checkAndTrackAchievement(Set<Achievement> unlocked, boolean condition, Achievement achievement) {
        if (condition && !unlocked.contains(achievement)) {
            unlocked.add(achievement);

            Counter.builder("logros_desbloqueados_total")
                    .description("Total de logros desbloqueados por los jugadores")
                    .tag("logro", achievement.name())
                    .register(meterRegistry)
                    .increment();
        }
    }
    
    private int calculateLevel(int points) {
        return Math.max(1, (points / POINTS_PER_LEVEL) + 1);
    }
}