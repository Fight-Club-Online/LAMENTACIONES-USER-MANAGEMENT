package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service;

import org.springframework.stereotype.Service;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.GetUserStatsUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.UserStatsRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.UserStats;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Servicio para las estadísticas del usuario instrumentado con métricas de consulta de negocio.
 */
@Service
public class UserStatsService implements GetUserStatsUseCase {

    private final UserStatsRepositoryPort repository;
    private final MeterRegistry meterRegistry; 
    public UserStatsService(UserStatsRepositoryPort repository, MeterRegistry meterRegistry) {
        this.repository = repository;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public UserStats getStats(String userId) {
        return repository.findByUserId(userId)
                .map(stats -> {
                    trackStatsQuery("exito");
                    return stats;
                })
                .orElseThrow(() -> {
                    trackStatsQuery("error");
                    return new java.util.NoSuchElementException("No se encontraron estadísticas para el usuario: " + userId);
                });
    }

    private void trackStatsQuery(String resultado) {
        Counter.builder("estadisticas_consultadas_total")
                .description("Total de consultas de lectura sobre las estadísticas competitivas de los usuarios")
                .tag("resultado", resultado)
                .register(meterRegistry)
                .increment();
    }
}