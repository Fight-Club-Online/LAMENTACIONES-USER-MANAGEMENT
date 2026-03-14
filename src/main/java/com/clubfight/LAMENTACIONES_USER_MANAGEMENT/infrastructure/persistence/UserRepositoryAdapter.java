package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.persistence;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.mappers.UserMapper;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.UserRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.User;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.enums.Role;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Adaptador del repositorio de un usuario.
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository mongoRepo;

    @Override
    public User save(User user) {
        var document = UserMapper.toDocument(user);
        var saved = mongoRepo.save(document);
        return UserMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return mongoRepo.findByEmail(email)
                        .map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findById(String userId) {
        return mongoRepo.findById(userId)
                        .map(UserMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return mongoRepo.existsByEmail(email);
    }

    @Override
    public Optional<User> findGuestByUsername(String username) {
        return mongoRepo.findByUsernameAndRole(username, Role.GUEST)
                        .map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByRefreshToken(String refreshToken) {
        return mongoRepo.findByRefreshToken(refreshToken)
                        .map(UserMapper::toDomain);
    }

    @Override
    public List<User> findActiveGuests(Instant now) {
        return mongoRepo.findByRoleAndGuestExpirationAfter(Role.GUEST, now)
                        .stream()
                        .map(UserMapper::toDomain)
                        .collect(Collectors.toList());
    }

    @Override
    public List<User> findExpiredGuests(Instant now) {
        return mongoRepo.findByRoleAndGuestExpirationBefore(Role.GUEST, now)
                        .stream()
                        .map(UserMapper::toDomain)
                        .collect(Collectors.toList());
    }

    @Override
    public void delete(User user) {
        mongoRepo.deleteById(user.getId());
    }

}