package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Command para eliminar el perfil de un usuario.
 */
@Data
@AllArgsConstructor
public class DeleteUserProfileCommand {
    private String userId;

}