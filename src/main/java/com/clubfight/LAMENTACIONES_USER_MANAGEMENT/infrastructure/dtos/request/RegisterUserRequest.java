package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de solicitud 
 * para registrar un usuario con perfil propio.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserRequest {
    private String email;
    private String username;
    private String password;
    private String avatarURL;
}