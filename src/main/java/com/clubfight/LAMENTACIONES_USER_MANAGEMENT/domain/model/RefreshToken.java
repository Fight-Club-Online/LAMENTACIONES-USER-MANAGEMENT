package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase para el token de refresco.
 */
@RedisHash(value = "refresh_tokens", timeToLive = 604800)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {

    @Id
    private String token;
    private String userId;
    private Instant expiration;
    
}
