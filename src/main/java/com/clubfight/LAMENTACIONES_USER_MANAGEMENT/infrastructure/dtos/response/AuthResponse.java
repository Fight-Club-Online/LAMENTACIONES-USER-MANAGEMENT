package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.response;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta de autenticación (login o registro).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String userId;
    private String username;
    private String email;
    private Role role;
}