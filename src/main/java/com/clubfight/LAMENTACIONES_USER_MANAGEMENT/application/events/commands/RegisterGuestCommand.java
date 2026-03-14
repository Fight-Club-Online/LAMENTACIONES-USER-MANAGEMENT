package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command para registrar un usuario invitado temporal.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterGuestCommand {
    private String username; 
}