package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.config;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.UserEventPublisher;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.UserPromotedToAdminEvent;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.UserRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.enums.Role;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.User;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializationConfig implements CommandLineRunner {

    private final UserRepositoryPort userRepositoryPort;
    private final UserEventPublisher userEventPublisher; 

    @Override
    public void run(String... args) {
        String adminEmail = "eresmiperrita@gmail.com";

        userRepositoryPort.findByEmail(adminEmail).ifPresentOrElse(
            user -> {
                if (user.getRole() != Role.ADMIN) {
                    user.setRole(Role.ADMIN);
                    user.setVerified(true);
                    userRepositoryPort.save(user);
                    
                    publishAdminEvent(user);
                    System.err.println(">>> [SECURITY] Usuario existente promovido a ADMIN y evento enviado.");
                } else {
                    System.err.println(">>> [SECURITY] El usuario ya es ADMIN.");
                }
            },
            () -> {
                User admin = new User();
                admin.setEmail(adminEmail);
                admin.setUsername("eresmiperrita");
                admin.setPassword("123456"); 
                admin.setRole(Role.ADMIN);
                admin.setVerified(true);
                
                userRepositoryPort.save(admin);
                
                publishAdminEvent(admin);
                System.err.println(">>> [SECURITY] Usuario ADMIN creado por el sistema y evento enviado.");
            }
        );
    }

    private void publishAdminEvent(User user) {
        UserPromotedToAdminEvent event = UserPromotedToAdminEvent.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
        
        userEventPublisher.publishUserPromotedToAdmin(event);
    }
}