package com.hackathon.finservice.Security;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class TokenBlacklistService {

    // Almacena los tokens invalidados (en memoria)
    private final Set<String> blacklist = new HashSet<>();

    // Agrega un token a la lista negra
    public void addToBlacklist(String token) {
        blacklist.add(token);
    }

    // Verifica si un token está en la lista negra
    public boolean isBlacklisted(String token) {
        return blacklist.contains(token);
    }
}