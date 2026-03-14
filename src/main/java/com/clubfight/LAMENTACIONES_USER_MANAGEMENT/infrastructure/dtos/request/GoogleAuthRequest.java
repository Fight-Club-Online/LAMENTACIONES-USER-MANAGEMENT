package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.request;

import lombok.Data;

/**
 * DTO de
 * 
 *  solicitud para la autenticación de google.
 */
@Data
public class GoogleAuthRequest {
    private String idToken;
}
