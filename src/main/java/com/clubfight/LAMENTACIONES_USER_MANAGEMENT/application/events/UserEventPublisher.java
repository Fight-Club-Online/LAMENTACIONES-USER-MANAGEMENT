package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import org.springframework.amqp.support.converter.MessageConverter;

import lombok.RequiredArgsConstructor;

/**
 * Evento que se envia a otros microservicios.
 */
@Component
@RequiredArgsConstructor
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    private final String USER_EXCHANGE = "user.events";

    public void publishUserRegistered(UserRegisteredEvent event) {
        rabbitTemplate.convertAndSend(USER_EXCHANGE, "user.registered", event);
    }

    public void publishUserLoggedIn(UserLoggedInEvent event) {
        rabbitTemplate.convertAndSend(USER_EXCHANGE, "user.loggedin", event);
    }

    public void publishGuestRegistered(GuestRegisteredEvent event) {
        rabbitTemplate.convertAndSend(USER_EXCHANGE, "user.guest.registered", event);
    }

    public void publishUserProfileUpdated(UserRegisteredEvent event) {
        rabbitTemplate.convertAndSend(USER_EXCHANGE, "user.profile.updated", event);
    }
     @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}