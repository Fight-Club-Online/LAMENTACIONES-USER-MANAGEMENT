package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.request;

import lombok.Data;

/**
 * DTO de solicitud para el token de refresco.
 */
@Data
public class RefreshRequest {
    private String refreshToken;

}