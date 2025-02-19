package com.hackathon.finservice.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTUtils {
    @Value("${jwt.secret}")
    private String secretKey; // Llave secreta para JWT
    private final TokenBlacklistService tokenBlacklistService; // Servicio para la lista negra

    public JWTUtils(TokenBlacklistService tokenBlacklistService) {
        this.tokenBlacklistService = tokenBlacklistService;
    }


    public String generateToken(String email) {
        // 1 hora
        long expiration = 3600000;
        String token;
        do {
            token = Jwts.builder()
                    .setSubject(email)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(SignatureAlgorithm.HS256, secretKey)
                    .compact();
        } while (tokenBlacklistService.isBlacklisted(token));
        return token;
    }

    public String extractEmail(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenValid(String token, String userEmail) {
        String extractedEmail = extractEmail(token);
        return extractedEmail.equals(userEmail) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }

}

