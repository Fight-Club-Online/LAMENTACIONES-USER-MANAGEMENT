package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de solicitud para login de usuario registrado.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserRequest {

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email no tiene un formato válido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;
}