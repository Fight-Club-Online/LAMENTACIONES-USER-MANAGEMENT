package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.response;

import lombok.Builder;
import lombok.Data;

/**
 * DTO de respuesta para actualizar el perfil de usuario.
 * 
 * 
 */
@Data
@Builder
public class UpdateProfileResponse {
    private String userId;
    private String username;
    private String country;
    private String avatarURL;
    private String city;
    private boolean notification;
}
