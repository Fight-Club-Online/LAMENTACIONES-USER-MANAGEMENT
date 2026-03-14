package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.RefreshTokenRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.UserRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.User;

import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Servicio para un usuario invitado.
 */
@Service
@RequiredArgsConstructor
public class GuestCleanupService {

    private final UserRepositoryPort userRepositoryPort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpiredGuests() {

        List<User> expiredGuests = userRepositoryPort.findExpiredGuests(Instant.now());

        for (User guest : expiredGuests) {

            refreshTokenRepositoryPort.deleteByUserId(guest.getId());

            userRepositoryPort.delete(guest);
        }
    }
}