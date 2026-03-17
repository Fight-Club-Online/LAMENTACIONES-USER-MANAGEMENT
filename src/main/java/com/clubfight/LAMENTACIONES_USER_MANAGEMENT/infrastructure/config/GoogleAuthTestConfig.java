package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service.GoogleAuthService;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.enums.Role;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.response.AuthResponse;

@Configuration
@Profile("staging")
public class GoogleAuthTestConfig {

    @Bean
    @Primary
    public GoogleAuthService googleAuthServiceMock() {
        return idToken -> AuthResponse.builder()
                .userId("google-test-user")
                .email("google-test@example.local")
                .accessToken("fake-access-token")
                .refreshToken("fake-refresh")
                .role(Role.ADMIN)
                .build();
    }
}
