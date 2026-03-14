package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.SaveUserProfileCommand;

/**
 * Caso de uso para guardar el perfile de un usuario.
 */
public interface SaveUserProfileUseCase {
    void saveUserProfile(SaveUserProfileCommand command);

}