package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Evento que se dispara cuando un usuario invitado se crea.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestRegisteredEvent {
    private String userId;
    private String username;
    private Instant createdAt;
    private Instant guestExpiration;
}