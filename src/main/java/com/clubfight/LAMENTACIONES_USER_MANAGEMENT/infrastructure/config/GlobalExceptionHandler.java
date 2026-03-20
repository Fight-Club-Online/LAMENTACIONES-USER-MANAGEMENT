package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildError(HttpStatus.BAD_REQUEST, "Datos inválidos", errors);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        String message = ex.getMessage();

        if (message == null) {
            return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno", "Error inesperado");
        }

        HttpStatus status = switch (message) {
            case "Usuario ya existe con ese email"    -> HttpStatus.CONFLICT;
            case "Usuario no encontrado"              -> HttpStatus.NOT_FOUND;
            case "Contraseña incorrecta"              -> HttpStatus.UNAUTHORIZED;
            case "Refresh token inválido",
                 "Refresh token expirado"             -> HttpStatus.UNAUTHORIZED;
            case "Perfil no encontrado para parchear" -> HttpStatus.NOT_FOUND;
            default -> message.contains("Token inválido") || message.contains("google")
                    ? HttpStatus.UNAUTHORIZED
                    : HttpStatus.INTERNAL_SERVER_ERROR;
        };

        return buildError(status, message, null);
    }

    private ResponseEntity<Map<String, Object>> buildError(HttpStatus status, String message, String detail) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        if (detail != null) body.put("detail", detail);
        return ResponseEntity.status(status).body(body);
    }
}