package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.unit.usecases;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service.GuestCleanupService;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.DeleteUserProfileUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.RefreshTokenRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.UserRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.User;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests para GuestCleanupService con soporte de instrumentación de métricas nativas.
 */
@ExtendWith(MockitoExtension.class)
class GuestCleanupServiceTest {

    private GuestCleanupService service;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    @Mock
    private DeleteUserProfileUseCase deleteUserProfileUseCase;

    private MeterRegistry meterRegistry; 

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();

        service = new GuestCleanupService(
                userRepositoryPort,
                refreshTokenRepositoryPort,
                deleteUserProfileUseCase,
                meterRegistry
        );
    }

    @Test
    void shouldDeleteExpiredGuests() {
        User guest1 = User.builder().id("g1").email("g1@gmail.com").build();
        User guest2 = User.builder().id("g2").email("g2@gmail.com").build();

        when(userRepositoryPort.findExpiredGuests(any(Instant.class)))
                .thenReturn(List.of(guest1, guest2));

        service.cleanupExpiredGuests();

        verify(refreshTokenRepositoryPort).deleteByUserId("g1");
        verify(refreshTokenRepositoryPort).deleteByUserId("g2");
        verify(deleteUserProfileUseCase).delete("g1");
        verify(deleteUserProfileUseCase).delete("g2");
        verify(userRepositoryPort).delete(guest1);
        verify(userRepositoryPort).delete(guest2);

        assertEquals(2.0, meterRegistry.counter("invitados_eliminados_total").count());
    }

    @Test
    void shouldDoNothingWhenNoExpiredGuests() {
        when(userRepositoryPort.findExpiredGuests(any(Instant.class)))
                .thenReturn(List.of());

        service.cleanupExpiredGuests();

        verify(refreshTokenRepositoryPort, never()).deleteByUserId(any());
        verify(deleteUserProfileUseCase, never()).delete(any());
        verify(userRepositoryPort, never()).delete(any());

        assertEquals(0.0, meterRegistry.counter("invitados_eliminados_total").count());
    }

    @Test
    void shouldTrackMetricsWhenDeletionFails() {
        User guest1 = User.builder().id("g1").email("g1@gmail.com").build();
        
        when(userRepositoryPort.findExpiredGuests(any(Instant.class)))
                .thenReturn(List.of(guest1));
        
        doThrow(new RuntimeException("Database error"))
                .when(refreshTokenRepositoryPort).deleteByUserId("g1");

        service.cleanupExpiredGuests();

        assertEquals(0.0, meterRegistry.counter("invitados_eliminados_total").count());
        
        assertEquals(1.0, meterRegistry.counter("invitados_eliminacion_errores_total", "error", "RuntimeException").count());
    }
}