package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Clase documento para el perfil de usuario.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "user_profiles")
public class UserProfileDocument {

    @Id
    private String userId;
    private String username;
    private String bio;
    private String country;
    private String avatarURL;
    private String city;
    private boolean notification;

}