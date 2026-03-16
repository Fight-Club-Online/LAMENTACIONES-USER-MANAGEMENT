package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.config;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.UserRepositoryPort;

import lombok.RequiredArgsConstructor;

/**
 * Filtro de autenticación JWT.
 * Se encarga de validar el token en cada petición, excepto en rutas públicas.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepositoryPort userRepositoryPort;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();

        if (path.contains("/api/v1/users/register") ||
            path.startsWith("/api/v1/users/login")   ||
            path.contains("/api/v1/users/guest")     ||
            path.contains("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                String userId = jwtUtil.extractUserId(token);

                if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    userRepositoryPort.findById(userId).ifPresent(user -> {
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(user, null, List.of());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    });
                }
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}