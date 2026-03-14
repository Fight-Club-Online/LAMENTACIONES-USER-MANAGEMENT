package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.response;

import lombok.Builder;
import lombok.Data;

/**
 * DTO de respuesta para el perfil del usuario.
 */
@Data
@Builder
public class UserProfileResponse {

    private String userId;
    private String username;
    private String bio;
    private String country;
    private String avatarURL;
    private String city;
    private boolean notification;
}

