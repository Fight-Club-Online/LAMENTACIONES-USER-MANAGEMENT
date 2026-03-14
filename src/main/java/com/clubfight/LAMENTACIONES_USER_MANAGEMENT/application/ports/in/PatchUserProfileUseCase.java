package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.PatchUserProfileCommand;

/**
 * Caso de uso para actulizar algunos campos del perfil del usuario.
 */
public interface PatchUserProfileUseCase {
    void patch(String userId, PatchUserProfileCommand command);

}
