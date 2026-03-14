package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Evento que se dispara cuando un usuario confirma su verificación de email.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVerifiedEvent {
    private String userId;
    private Instant verifiedAt;
}