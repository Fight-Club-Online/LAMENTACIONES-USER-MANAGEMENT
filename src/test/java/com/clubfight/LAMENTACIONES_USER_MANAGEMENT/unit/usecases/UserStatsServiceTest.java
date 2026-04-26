package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.unit.usecases;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.UserStatsRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service.UserStatsService;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.UserStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserStatsServiceTest {

    @InjectMocks
    private UserStatsService service;

    @Mock
    private UserStatsRepositoryPort repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
    }

    @Test
    void shouldThrowWhenStatsMissing() {
        String userId = "missing";

        when(repository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.getStats(userId));
        verify(repository, times(1)).findByUserId(userId);
    }
}