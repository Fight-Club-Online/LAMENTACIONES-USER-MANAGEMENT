package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de solicitud 
 * para actualizar el perfil del usuario.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    private String bio;
    private String country;
    private String avatarURL;
    private String city;   
    private boolean notifications;
}