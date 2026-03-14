package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Evento que se dispara cuando un usuario invitado expira o es eliminado.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDeletedEvent {
    private String userId;
    private Instant deletedAt;
}