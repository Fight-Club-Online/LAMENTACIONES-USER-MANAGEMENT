package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events;

import java.time.Instant;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Evento que se dispara cuando un usuario se registra correctamente.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent {
    private String userId;
    private String email;
    private String username;
    private Role role;
    private Instant createdAt;
}