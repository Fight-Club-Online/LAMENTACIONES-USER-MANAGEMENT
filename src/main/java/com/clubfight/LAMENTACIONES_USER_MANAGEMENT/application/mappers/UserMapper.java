package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.mappers;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.User;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.UserProfile;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.response.AuthResponse;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.response.UserProfileResponse;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.persistence.UserDocument;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.UserLoggedInEvent;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.UserRegisteredEvent;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.GuestRegisteredEvent;

/**
 * Mapper para el usuario.
 */
public class UserMapper {

    public static UserDocument toDocument(User user) {
        if (user == null) return null;
        return UserDocument.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .verified(user.isVerified())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .refreshToken(user.getRefreshToken())
                .guestExpiration(user.getGuestExpiration())
                .build();
    }

    public static User toDomain(UserDocument doc) {
        if (doc == null) return null;
        return User.builder()
                .id(doc.getId())
                .email(doc.getEmail())
                .password(doc.getPassword())
                .verified(doc.isVerified())
                .role(doc.getRole())
                .createdAt(doc.getCreatedAt())
                .lastLogin(doc.getLastLogin())
                .refreshToken(doc.getRefreshToken())
                .guestExpiration(doc.getGuestExpiration())
                .build();
    }

    public static AuthResponse toAuthResponse(User user, String accessToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public static UserRegisteredEvent toUserRegisteredEvent(User user) {
        return UserRegisteredEvent.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public static UserLoggedInEvent toUserLoggedInEvent(User user, String token) {
        return UserLoggedInEvent.builder()
                .userId(user.getId())
                .loginAt(user.getLastLogin())
                .token(token)
                .build();
    }

    public static GuestRegisteredEvent toGuestRegisteredEvent(User guest) {
        return GuestRegisteredEvent.builder()
                .userId(guest.getId())
                .createdAt(guest.getCreatedAt())
                .guestExpiration(guest.getGuestExpiration())
                .build();
    }

    public static UserProfileResponse toUserProfileResponse(UserProfile profile) {
        return UserProfileResponse.builder()
                .userId(profile.getUserId())
                .username(profile.getUsername())
                .bio(profile.getBio())
                .country(profile.getCountry())
                .avatarURL(profile.getAvatarURL())
                .city(profile.getCity())
                .notification(profile.isNotification())
                .build();
    }

    public static UserRegisteredEvent toUserProfileUpdatedEvent(User user) {
        return UserRegisteredEvent.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}