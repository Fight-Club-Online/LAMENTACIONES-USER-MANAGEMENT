package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Evento que se dispara cuando un usuario inicia sesión correctamente.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoggedInEvent implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private String userId;
    private Instant loginAt;
    private String token; 
}