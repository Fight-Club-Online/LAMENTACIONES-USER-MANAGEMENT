package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.persistence;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase documento de un usuario registrado o invitado en el sistema.
 */

@Document(collection = "users")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDocument {

    @Id
    private String id;
    private String email;
    private String password;
    private String username;
    private Role role;
    private boolean verified; 
    private Instant createdAt; 
    private Instant lastLogin; 
    private String refreshToken; 
    private Instant guestExpiration; 

    public static class UserBuilder {
        private Instant createdAt = Instant.now();
    }
}