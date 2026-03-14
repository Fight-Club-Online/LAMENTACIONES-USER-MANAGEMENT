package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de solicitud para login de usuario registrado.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserRequest {
    private String email;
    private String password;
}