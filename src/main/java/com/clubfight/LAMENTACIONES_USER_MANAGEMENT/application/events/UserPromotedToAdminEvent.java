package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPromotedToAdminEvent {
    private String userId;
    private String email;
    private String username;
    private String role; 
}