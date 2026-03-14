package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.mappers.UserProfileMapper;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.UserProfileRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.UserProfile;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.repositories.UserProfileRepository;

import lombok.RequiredArgsConstructor;

/**
 * Clase adaptador para el repositorio del perfil de un usuario.
 */
@Repository
@RequiredArgsConstructor
public class UserProfileRepositoryAdapter implements UserProfileRepositoryPort {

    private final UserProfileRepository mongoRepo;
    private final UserProfileMapper mapper;

    @Override
    public UserProfile save(UserProfile profile) {

        var document = mapper.toDocument(profile);
        var saved = mongoRepo.save(document);

        return mapper.toDomain(saved);
    }

    @Override
    public Optional<UserProfile> findByUserId(String userId) {

        return mongoRepo.findByUserId(userId)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteByUserId(String userId) {
        mongoRepo.deleteByUserId(userId);
    }
}