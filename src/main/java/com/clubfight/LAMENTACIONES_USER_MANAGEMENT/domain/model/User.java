package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model;

import java.time.Instant;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.enums.Role;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.enums.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Clase que representa un usuario registrado en el sistema.
 */

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String email;
    private String password;
    private UserStatus status;
    private boolean verified;
    private String refreshToken;
    private Role role;
    private Instant createdAt;
    private Instant lastLogin;
    private Instant guestExpiration;
}
