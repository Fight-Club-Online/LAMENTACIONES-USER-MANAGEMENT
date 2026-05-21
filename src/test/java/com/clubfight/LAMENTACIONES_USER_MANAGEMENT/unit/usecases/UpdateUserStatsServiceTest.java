package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.unit.usecases;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.UserStatsRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service.UpdateUserStatsService;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.enums.Achievement;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.enums.Rank;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.UserStats;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests para UpdateUserStatsService con soporte de instrumentación de métricas de combates reales.
 */
@ExtendWith(MockitoExtension.class)
class UpdateUserStatsServiceTest {

    private UpdateUserStatsService service;

    @Mock
    private UserStatsRepositoryPort repository;

    private MeterRegistry meterRegistry; 

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();

        service = new UpdateUserStatsService(repository, meterRegistry);
    }

    private UserStats statsOf(String userId, int wins, int losses, int draws,
                               int totalFights, int points, int streak) {
        return new UserStats(userId, wins, losses, draws, 0,
                totalFights, points, 1, streak,
                Rank.fromPoints(points), new HashSet<>());
    }

    private UserStats capturedSave() {
        ArgumentCaptor<UserStats> captor = ArgumentCaptor.forClass(UserStats.class);
        verify(repository, atLeastOnce()).save(captor.capture());
        return captor.getValue();
    }

    @Test
    void shouldCreateStatsFromScratchOnFirstWin() {
        when(repository.findByUserId("new-user")).thenReturn(Optional.empty());

        service.applyFightResult("new-user", true, false, 20);

        UserStats saved = capturedSave();
        assertEquals(1, saved.getWins());
        assertEquals(0, saved.getLosses());
        assertEquals(0, saved.getDraws());
        assertEquals(1, saved.getTotalFights());
        assertEquals(20, saved.getPoints());
        assertEquals(1, saved.getStreak());
        assertEquals(Rank.HIERRO_I, saved.getRank());
        assertEquals(1, saved.getLevel());

        assertEquals(1.0, meterRegistry.counter("peleas_procesadas_total", "resultado", "victoria", "rango", "HIERRO_I").count());
        assertEquals(1.0, meterRegistry.counter("logros_desbloqueados_total", "logro", "PRIMERA_SANGRE").count());
    }

    @Test
    void shouldCreateStatsFromScratchOnFirstLoss() {
        when(repository.findByUserId("new-loser")).thenReturn(Optional.empty());

        service.applyFightResult("new-loser", false, false, -10);

        UserStats saved = capturedSave();
        assertEquals(0, saved.getWins());
        assertEquals(1, saved.getLosses());
        assertEquals(0, saved.getStreak());
        assertEquals(0, saved.getPoints()); 
        
        assertEquals(1.0, meterRegistry.counter("peleas_procesadas_total", "resultado", "derrota", "rango", "HIERRO_I").count());
    }

    @Test
    void shouldCreateStatsFromScratchOnFirstDraw() {
        when(repository.findByUserId("new-draw")).thenReturn(Optional.empty());

        service.applyFightResult("new-draw", false, true, 5);

        UserStats saved = capturedSave();
        assertEquals(0, saved.getWins());
        assertEquals(0, saved.getLosses());
        assertEquals(1, saved.getDraws());
        assertEquals(0, saved.getStreak());
        assertEquals(5, saved.getPoints());
        
        assertEquals(1.0, meterRegistry.counter("peleas_procesadas_total", "resultado", "empate", "rango", "HIERRO_I").count());
    }

    @Test
    void shouldIncrementWinsAndStreakOnWin() {
        UserStats existing = statsOf("u1", 3, 1, 0, 4, 200, 2);
        when(repository.findByUserId("u1")).thenReturn(Optional.of(existing));

        service.applyFightResult("u1", true, false, 30);

        UserStats saved = capturedSave();
        assertEquals(4, saved.getWins());
        assertEquals(1, saved.getLosses());
        assertEquals(3, saved.getStreak());
        assertEquals(5, saved.getTotalFights());
        assertEquals(230, saved.getPoints());
    }

    @Test
    void shouldIncrementLossesAndResetStreakOnLoss() {
        UserStats existing = statsOf("u2", 5, 0, 0, 5, 300, 5);
        when(repository.findByUserId("u2")).thenReturn(Optional.of(existing));

        service.applyFightResult("u2", false, false, -20);

        UserStats saved = capturedSave();
        assertEquals(5, saved.getWins());
        assertEquals(1, saved.getLosses());
        assertEquals(0, saved.getStreak());
        assertEquals(280, saved.getPoints());
    }

    @Test
    void shouldNotGoBelowZeroPoints() {
        UserStats existing = statsOf("u3", 0, 0, 0, 0, 5, 0);
        when(repository.findByUserId("u3")).thenReturn(Optional.of(existing));

        service.applyFightResult("u3", false, false, -50);

        UserStats saved = capturedSave();
        assertEquals(0, saved.getPoints());
    }

    @Test
    void shouldIncrementDrawsAndResetStreakOnDraw() {
        UserStats existing = statsOf("u4", 2, 0, 1, 3, 150, 3);
        when(repository.findByUserId("u4")).thenReturn(Optional.of(existing));

        service.applyFightResult("u4", false, true, 10);

        UserStats saved = capturedSave();
        assertEquals(2, saved.getDraws());
        assertEquals(0, saved.getStreak());
        assertEquals(160, saved.getPoints());
    }

    @Test
    void shouldCalculateLevelCorrectly() {
        UserStats existing = statsOf("u-level", 0, 0, 0, 0, 0, 0);
        when(repository.findByUserId("u-level")).thenReturn(Optional.of(existing));

        service.applyFightResult("u-level", true, false, 250);

        UserStats saved = capturedSave();
        assertEquals(3, saved.getLevel()); 
    }

    @Test
    void shouldNeverGoBelowLevel1() {
        UserStats existing = statsOf("u-minlevel", 0, 0, 0, 0, 0, 0);
        when(repository.findByUserId("u-minlevel")).thenReturn(Optional.of(existing));

        service.applyFightResult("u-minlevel", false, false, 0);

        UserStats saved = capturedSave();
        assertEquals(1, saved.getLevel());
    }
    
    @Test
    void shouldUpdateRankBasedOnPoints() {
        UserStats existing = statsOf("u-rank", 0, 0, 0, 0, 0, 0);
        
        when(repository.findByUserId("u-rank")).thenReturn(Optional.of(existing));

        service.applyFightResult("u-rank", true, false, 350);
        
        UserStats saved = capturedSave();
        assertEquals(Rank.BRONCE_I, saved.getRank());
    }

    @Test
    void shouldReachPlatinoAt1500Points() {
        UserStats existing = statsOf("u-plat", 0, 0, 0, 0, 1490, 0);
        when(repository.findByUserId("u-plat")).thenReturn(Optional.of(existing));

        service.applyFightResult("u-plat", true, false, 10);

        UserStats saved = capturedSave();
        assertEquals(Rank.PLATINO, saved.getRank());
    }

    @Test
    void shouldUnlockPrimeraSangreOnFirstWin() {
        when(repository.findByUserId("u-ps")).thenReturn(Optional.empty());

        service.applyFightResult("u-ps", true, false, 20);

        UserStats saved = capturedSave();
        assertTrue(saved.getAchievements().contains(Achievement.PRIMERA_SANGRE));
        assertEquals(1.0, meterRegistry.counter("logros_desbloqueados_total", "logro", "PRIMERA_SANGRE").count());
    }

    @Test
    void shouldNotUnlockPrimeraSangreOnLoss() {
        when(repository.findByUserId("u-nops")).thenReturn(Optional.empty());

        service.applyFightResult("u-nops", false, false, -10);

        UserStats saved = capturedSave();
        falseUp(saved.getAchievements().contains(Achievement.PRIMERA_SANGRE));
        assertEquals(0.0, meterRegistry.counter("logros_desbloqueados_total", "logro", "PRIMERA_SANGRE").count());
    }

    private void falseUp(boolean condition) {
        assertFalse(condition);
    }

    @Test
    void shouldUnlockVeteranoAt50Fights() {
        UserStats existing = statsOf("u-vet", 30, 15, 4, 49, 400, 0);
        when(repository.findByUserId("u-vet")).thenReturn(Optional.of(existing));

        service.applyFightResult("u-vet", true, false, 20);

        UserStats saved = capturedSave();
        assertTrue(saved.getAchievements().contains(Achievement.VETERANO));
        assertEquals(1.0, meterRegistry.counter("logros_desbloqueados_total", "logro", "VETERANO").count());
    }

    @Test
    void shouldNotUnlockVeteranoBelow50Fights() {
        UserStats existing = statsOf("u-novet", 20, 10, 5, 35, 300, 0);
        when(repository.findByUserId("u-novet")).thenReturn(Optional.of(existing));

        service.applyFightResult("u-novet", true, false, 20);

        UserStats saved = capturedSave();
        assertFalse(saved.getAchievements().contains(Achievement.VETERANO));
    }

    @Test
    void shouldUnlockLeyendaAt100Wins() {
        UserStats existing = statsOf("u-ley", 99, 10, 5, 114, 800, 3);
        when(repository.findByUserId("u-ley")).thenReturn(Optional.of(existing));

        service.applyFightResult("u-ley", true, false, 20);

        UserStats saved = capturedSave();
        assertTrue(saved.getAchievements().contains(Achievement.LEYENDA));
        assertEquals(1.0, meterRegistry.counter("logros_desbloqueados_total", "logro", "LEYENDA").count());
    }

    @Test
    void shouldUnlockRachaDe5OnStreak5() {
        UserStats existing = statsOf("u-r5", 4, 0, 0, 4, 200, 4);
        when(repository.findByUserId("u-r5")).thenReturn(Optional.of(existing));

        service.applyFightResult("u-r5", true, false, 20);

        UserStats saved = capturedSave();
        assertTrue(saved.getAchievements().contains(Achievement.RACHA_DE_5));
        assertFalse(saved.getAchievements().contains(Achievement.RACHA_DE_10));
        assertEquals(1.0, meterRegistry.counter("logros_desbloqueados_total", "logro", "RACHA_DE_5").count());
    }

    @Test
    void shouldUnlockRachaDe10OnStreak10() {
        UserStats existing = statsOf("u-r10", 9, 0, 0, 9, 500, 9);
        when(repository.findByUserId("u-r10")).thenReturn(Optional.of(existing));

        service.applyFightResult("u-r10", true, false, 20);

        UserStats saved = capturedSave();
        assertTrue(saved.getAchievements().contains(Achievement.RACHA_DE_5));
        assertTrue(saved.getAchievements().contains(Achievement.RACHA_DE_10));
        assertEquals(1.0, meterRegistry.counter("logros_desbloqueados_total", "logro", "RACHA_DE_10").count());
    }

    @Test
    void shouldUnlockMaestroDelRingAtPlatino() {
        UserStats existing = statsOf("u-maestro", 50, 10, 5, 65, 1490, 0);
        when(repository.findByUserId("u-maestro")).thenReturn(Optional.of(existing));

        service.applyFightResult("u-maestro", true, false, 10);

        UserStats saved = capturedSave();
        assertEquals(Rank.PLATINO, saved.getRank());
        assertTrue(saved.getAchievements().contains(Achievement.MAESTRO_DEL_RING));
        assertEquals(1.0, meterRegistry.counter("logros_desbloqueados_total", "logro", "MAESTRO_DEL_RING").count());
    }

    @Test
    void shouldHandleNullAchievementsSetGracefully() {
        UserStats existing = new UserStats("u-null-ach", 0, 0, 0, 0, 0, 0, 1, 0, Rank.HIERRO_I, null);
        when(repository.findByUserId("u-null-ach")).thenReturn(Optional.of(existing));

        assertDoesNotThrow(() -> service.applyFightResult("u-null-ach", true, false, 20));

        UserStats saved = capturedSave();
        assertNotNull(saved.getAchievements());
        assertTrue(saved.getAchievements().contains(Achievement.PRIMERA_SANGRE));
    }

    @Test
    void shouldAlwaysCallRepositorySave() {
        when(repository.findByUserId(any())).thenReturn(Optional.empty());

        service.applyFightResult("u-save", true, false, 10);
        service.applyFightResult("u-save2", false, false, -5);
        service.applyFightResult("u-save3", false, true, 5);

        verify(repository, times(3)).save(any(UserStats.class));
    }
}