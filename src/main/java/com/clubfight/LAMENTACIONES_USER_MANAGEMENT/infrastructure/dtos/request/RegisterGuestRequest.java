package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de solicitud 
 * para registrar un usuario invitado temporal.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterGuestRequest {
    private String username; 
}