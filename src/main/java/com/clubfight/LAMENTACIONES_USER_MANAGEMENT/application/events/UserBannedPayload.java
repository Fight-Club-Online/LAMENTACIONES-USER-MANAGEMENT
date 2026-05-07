package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events;

import lombok.Data;
import java.time.Instant;

@Data
public class UserBannedPayload {
    private String userId;
    private String status;   
    private String reason;
    private Instant expiresAt;
}