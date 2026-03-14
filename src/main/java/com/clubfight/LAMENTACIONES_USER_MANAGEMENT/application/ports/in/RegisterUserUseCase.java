package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.RegisterUserCommand;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.response.AuthResponse;

/**
 * Caso de uso para registrar un usuario permanente con perfil propio.
 */
public interface RegisterUserUseCase {
    AuthResponse register(RegisterUserCommand command); 
}
