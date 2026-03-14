package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands;

import lombok.Data;

/**
 * Command para actualizar algún campo del perfil del usuario.
 */
@Data
public class PatchUserProfileCommand {

    private String bio;
    private String country;
    private String avatarURL;
    private String city;
    private Boolean notification;

}