// UserBannedPayload.java — en el módulo de auth (user management)
package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events;

import lombok.Data;
import java.time.Instant;

@Data
public class UserBannedPayload {
    private String userId;
    private String status;   // String, no enum — evita problemas de deserialización
    private String reason;
    private Instant expiresAt;
}