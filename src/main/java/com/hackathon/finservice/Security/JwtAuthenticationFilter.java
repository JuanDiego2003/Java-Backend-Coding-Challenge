package com.hackathon.finservice.Security;

import com.hackathon.finservice.Service.UserService;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtils jwtUtils;
    private final UserService userService;
    private final TokenBlacklistService tokenBlacklistService; // Servicio para la lista negra

    public JwtAuthenticationFilter(JWTUtils jwtUtils, UserService userService, TokenBlacklistService tokenBlacklistService) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String token = getTokenFromRequest(request);
        String requestPath = request.getServletPath();

        // Permitir rutas públicas sin necesidad de autenticación
        if ("/api/users/register".equals(requestPath) || "/api/users/login".equals(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Verificar si el token está presente
        if (token != null) {
            // Verificar si el token está en la lista negra (es inválido)
            if (tokenBlacklistService.isBlacklisted(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("El token ha sido invalidado.");
                return;
            }

            // Verificar si el token es válido
            if (jwtUtils.isTokenValid(token, getUserEmailFromToken(token))) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(getUserEmailFromToken(token), null, Collections.singletonList(new SimpleGrantedAuthority("USER")));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Continuar con el procesamiento del filtro
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private String getUserEmailFromToken(String token) {
        return jwtUtils.extractEmail(token);
    }
}