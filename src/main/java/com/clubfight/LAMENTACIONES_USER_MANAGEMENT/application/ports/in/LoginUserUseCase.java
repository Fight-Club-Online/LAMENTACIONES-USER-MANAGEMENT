package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.LoginUserCommand;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.response.AuthResponse;

/**
 * Caso de uso para autenticar un usuario.
 */
public interface LoginUserUseCase {
    AuthResponse login(LoginUserCommand command);
}