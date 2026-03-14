package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.mappers;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.SaveUserProfileCommand;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.UpdateUserProfileCommand;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.PatchUserProfileCommand;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.UserProfile;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.persistence.UserProfileDocument;
import org.springframework.stereotype.Component;

/**
 * Mapper para el perfil del usuario.
 */
@Component
public class UserProfileMapper {

    public UserProfile toDomain(UserProfileDocument document) {
        return new UserProfile(
                document.getUserId(),
                document.getUsername(),
                document.getBio(),
                document.getCountry(),
                document.getAvatarURL(),
                document.getCity(),
                document.isNotification()
        );
    }

    public UserProfileDocument toDocument(UserProfile profile) {
        return new UserProfileDocument(
                profile.getUserId(),
                profile.getUsername(),
                profile.getBio(),
                profile.getCountry(),
                profile.getAvatarURL(),
                profile.getCity(),
                profile.isNotification()
        );
    }

    public UserProfile fromSaveCommand(SaveUserProfileCommand command) {
        return new UserProfile(
                command.getUserId(),
                command.getUsername(),
                command.getBio(),
                command.getCountry(),
                command.getAvatarURL(),
                command.getCity(),
                command.isNotification()
        );
    }

    public UserProfile fromUpdateCommand(UpdateUserProfileCommand command) {
        return new UserProfile(
                command.getUserId(),
                command.getUsername(),
                command.getBio(),
                command.getCountry(),
                command.getAvatarURL(),
                command.getCity(),
                command.isNotification()
        );
    }

    public void patchDocument(UserProfileDocument document, PatchUserProfileCommand command) {

        if (command.getBio() != null) {
            document.setBio(command.getBio());
        }

        if (command.getCountry() != null) {
            document.setCountry(command.getCountry());
        }

        if (command.getAvatarURL() != null) {
            document.setAvatarURL(command.getAvatarURL());
        }

        if (command.getCity() != null) {
            document.setCity(command.getCity());
        }

        if (command.getNotification() != null) {
            document.setNotification(command.getNotification());
        }
    }
}