package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.LoginUserUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.RegisterGuestUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.RegisterUserUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.request.LoginUserRequest;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.request.RegisterGuestRequest;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.request.RegisterUserRequest;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.response.AuthResponse;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.LoginUserCommand;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.RegisterGuestCommand;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.RegisterUserCommand;

import lombok.RequiredArgsConstructor;

/**
 * Controlador para la autenticación ded usuarios.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final RegisterGuestUseCase registerGuestUseCase;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody RegisterUserRequest request) {
        RegisterUserCommand command = RegisterUserCommand.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(request.getPassword())
                .build();

        AuthResponse response = registerUserUseCase.register(command);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody LoginUserRequest request) {
        LoginUserCommand command = LoginUserCommand.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        AuthResponse response = loginUserUseCase.login(command);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/guest")
    public ResponseEntity<AuthResponse> registerGuest(@RequestBody RegisterGuestRequest request) {
        RegisterGuestCommand command = RegisterGuestCommand.builder()
                .username(request.getUsername())
                .build();

        AuthResponse response = registerGuestUseCase.registerGuest(command);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}