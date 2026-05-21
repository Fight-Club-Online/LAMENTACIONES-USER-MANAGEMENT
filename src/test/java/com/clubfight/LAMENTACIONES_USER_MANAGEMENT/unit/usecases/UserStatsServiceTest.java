package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.unit.usecases;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.UserStatsRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service.UserStatsService;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.UserStats;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests para UserStatsService con soporte de instrumentación de métricas de consulta de estadísticas reales.
 */
@ExtendWith(MockitoExtension.class)
class UserStatsServiceTest {

    private UserStatsService service;

    @Mock
    private UserStatsRepositoryPort repository;

    private MeterRegistry meterRegistry; 

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();

        service = new UserStatsService(repository, meterRegistry);
    }

    @Test
    void shouldReturnStats() {
        String userId = "juan";

        UserStats stats = mock(UserStats.class);
        when(stats.getUserId()).thenReturn(userId);
        when(stats.getWins()).thenReturn(10);

        when(repository.findByUserId(userId)).thenReturn(Optional.of(stats));

        UserStats result = service.getStats(userId);

        assertNotNull(result);
        assertEquals(10, result.getWins());
        assertEquals(userId, result.getUserId());
        verify(repository, times(1)).findByUserId(userId);

        assertEquals(1.0, meterRegistry.counter("estadisticas_consultadas_total", "resultado", "exito").count());
    }

    @Test
    void shouldThrowWhenStatsMissing() {
        String userId = "missing";

        when(repository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.getStats(userId));
        verify(repository, times(1)).findByUserId(userId);

        assertEquals(1.0, meterRegistry.counter("estadisticas_consultadas_total", "resultado", "error").count());
    }
}