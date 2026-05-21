package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.DeleteUserProfileUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.RefreshTokenRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.UserRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.User;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import java.time.Instant;
import java.util.List;

/**
 * Servicio para un usuario invitado con telemetría de purga automatizada.
 */
@Service
public class GuestCleanupService {

    private final UserRepositoryPort userRepositoryPort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final DeleteUserProfileUseCase deleteUserProfileUseCase;
    private final MeterRegistry meterRegistry;

    public GuestCleanupService(
            UserRepositoryPort userRepositoryPort,
            RefreshTokenRepositoryPort refreshTokenRepositoryPort,
            DeleteUserProfileUseCase deleteUserProfileUseCase,
            MeterRegistry meterRegistry) {
        this.userRepositoryPort = userRepositoryPort;
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
        this.deleteUserProfileUseCase = deleteUserProfileUseCase;
        this.meterRegistry = meterRegistry;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpiredGuests() {
        
        List<User> expiredGuests = userRepositoryPort.findExpiredGuests(Instant.now());

        for (User guest : expiredGuests) {
            try {
                refreshTokenRepositoryPort.deleteByUserId(guest.getId());
            
                deleteUserProfileUseCase.delete(guest.getId());

                userRepositoryPort.delete(guest);

                Counter.builder("invitados_eliminados_total")
                        .description("Total de cuentas de usuarios invitados que expiraron y fueron limpiadas")
                        .register(meterRegistry)
                        .increment();

            } catch (Exception e) {
                Counter.builder("invitados_eliminacion_errores_total")
                        .description("Total de fallos ocurridos durante la limpieza de usuarios invitados")
                        .tag("error", e.getClass().getSimpleName())
                        .register(meterRegistry)
                        .increment();
                
            }
        }
    }
}