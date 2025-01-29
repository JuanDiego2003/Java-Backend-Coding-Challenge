package com.hackathon.finservice.Security;

import com.hackathon.finservice.Entities.Token;
import com.hackathon.finservice.Repositories.TokenRepository;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {
    public final TokenRepository tokenRepository;

    public TokenBlacklistService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    // Agrega un token a la lista negra
    public void addToBlacklist(String tokenUsed) {
        Token token = new Token(tokenUsed);
        tokenRepository.save(token);
    }

    // Verifica si un token está en la lista negra
    public boolean isBlacklisted(String tokenUsed) {
        Token token = tokenRepository.findByToken(tokenUsed);
        return token != null;
    }
}