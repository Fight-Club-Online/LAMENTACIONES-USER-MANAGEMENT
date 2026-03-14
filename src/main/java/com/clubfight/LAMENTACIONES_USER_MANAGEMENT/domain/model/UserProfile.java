package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase para el perfil del usuario.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {
    private String userId;
    private String username;
    private String bio;
    private String country;
    private String avatarURL;
    private String city;
    private boolean notification;
    
}
