package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command para registrar un usuario con perfil propio.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserCommand {
    private String email;
    private String username;
    private String password;
    private String avatarURL; 
}