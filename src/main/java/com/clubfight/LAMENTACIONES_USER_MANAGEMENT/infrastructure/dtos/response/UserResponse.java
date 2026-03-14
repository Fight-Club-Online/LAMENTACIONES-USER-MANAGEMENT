package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.response;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.enums.Role;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.enums.UserStatus;

import lombok.Builder;
import lombok.Data;

/**
 * DTO de respuesta para el usuario
 * 
 */
@Data
@Builder
public class UserResponse {
    private String id;
    private String email;
    private String username;
    private Role role;
    private UserStatus status;
    private boolean verified;
}
