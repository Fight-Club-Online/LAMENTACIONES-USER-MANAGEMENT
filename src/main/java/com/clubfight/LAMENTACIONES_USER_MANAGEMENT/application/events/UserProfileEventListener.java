package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.SaveUserProfileCommand;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.SaveUserProfileUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.config.RabbitConfig; // Importa tus constantes de colas
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserProfileEventListener {

    private final SaveUserProfileUseCase saveUserProfileUseCase;

    /**
     * Escucha registros de usuarios normales (Manual y Google).
     */
    @RabbitListener(queues = RabbitConfig.REGISTERED_QUEUE)
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("🚀 Creando perfil para Usuario Real: {}", event.getUsername());
        
        SaveUserProfileCommand command = new SaveUserProfileCommand();
        command.setUserId(event.getUserId());
        command.setUsername(event.getUsername());
        command.setAvatarURL(event.getAvatarURL() != null ? event.getAvatarURL() : "default_player.png");
        command.setNotification(true);
        
        saveUserProfileUseCase.saveUserProfile(command);
    }

    /**
     * Escucha registros de invitados temporales.
     */
    @RabbitListener(queues = "user.guest.registered.queue") 
    public void handleGuestRegistered(GuestRegisteredEvent event) {
        log.info("👤 Creando perfil para Invitado: {}", event.getUsername());
        
        SaveUserProfileCommand command = new SaveUserProfileCommand();
        command.setUserId(event.getUserId());
        command.setUsername(event.getUsername());
        command.setAvatarURL("https://api.dicebear.com/7.x/pixel-art/svg?seed=" + event.getUserId());
        command.setBio("Luchador invitado. Expira pronto.");
        command.setNotification(false); 
        
        saveUserProfileUseCase.saveUserProfile(command);
    }
}