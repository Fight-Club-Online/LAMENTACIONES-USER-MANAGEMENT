package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * Configuración para rabbitmq.
 */
@Configuration
public class RabbitConfig {

    public static final String USER_EXCHANGE = "user.events";
    public static final String REGISTERED_QUEUE = "user.registered.queue";
    public static final String LOGGEDIN_QUEUE = "user.loggedin.queue";
    public static final String GUEST_REGISTERED_QUEUE = "user.guest.registered.queue";

    @Bean
    public Queue guestRegisteredQueue() {
        return new Queue(GUEST_REGISTERED_QUEUE, true);
    }

    @Bean
    public Queue registeredQueue() {
        return new Queue(REGISTERED_QUEUE, true);
    }

    @Bean
    public Queue loggedInQueue() {
        return new Queue(LOGGEDIN_QUEUE, true);
    }

    @Bean
    public DirectExchange userExchange() {
        return new DirectExchange(USER_EXCHANGE);
    }
    @Bean
    public Binding bindingGuestRegistered() {
        return BindingBuilder.bind(guestRegisteredQueue())
                .to(userExchange())
                .with("user.guest.registered");
    }
    
    @Bean
    public Binding bindingRegistered() {
        return BindingBuilder.bind(registeredQueue())
                .to(userExchange())
                .with("user.registered");
    }

    @Bean
    public Binding bindingLoggedIn() {
        return BindingBuilder.bind(loggedInQueue())
                .to(userExchange())
                .with("user.loggedin");
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}