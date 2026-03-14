package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.UpdateUserProfileCommand;

/**
 * Caso de uso para actualizar el perfil de un usuario.
 */
public interface UpdateUserProfileUseCase {
    void update(UpdateUserProfileCommand command);

}
