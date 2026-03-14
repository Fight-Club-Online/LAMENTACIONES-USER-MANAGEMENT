package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command para login de usuario registrado.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserCommand {
    private String email;
    private String password;
}