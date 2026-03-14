package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.request;

import lombok.Builder;
import lombok.Data;

/**
 * DTO de solicitud para crear un usuario.
 */
@Data
@Builder
public class CreateUserRequest {
    private String email;
    private String password;
    private String username;

}

