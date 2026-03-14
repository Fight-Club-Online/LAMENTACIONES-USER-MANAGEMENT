package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out;

import java.util.Optional;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.RefreshToken;

public interface RefreshTokenRepositoryPort {

    RefreshToken save(RefreshToken token);

    Optional<RefreshToken> findById(String token);

    void delete(RefreshToken token);

    void deleteByUserId(String userId);
}