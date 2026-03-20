package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de solicitud 
 * para registrar un usuario invitado temporal.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterGuestRequest {

    @NotBlank(message = "El username no puede estar vacío")
    @Size(min = 3, max = 35, message = "El username debe tener entre 3 y 35 caracteres")
    private String username; 
}