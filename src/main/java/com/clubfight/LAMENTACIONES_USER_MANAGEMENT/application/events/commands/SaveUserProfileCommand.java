package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command para guardar el perfil de un usuario.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveUserProfileCommand {
    private String userId;
    private String username;
    private String bio;
    private String country;
    private String avatarURL;
    private String city;
    private boolean notification;
}
