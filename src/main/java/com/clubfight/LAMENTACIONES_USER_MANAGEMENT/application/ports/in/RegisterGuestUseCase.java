package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.RegisterGuestCommand;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.response.AuthResponse;

/**
 * Caso de uso para crear un usuario invitado temporal.
 */
public interface RegisterGuestUseCase {
    AuthResponse registerGuest(RegisterGuestCommand command);
}