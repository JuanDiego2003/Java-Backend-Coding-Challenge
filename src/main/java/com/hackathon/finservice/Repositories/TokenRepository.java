package com.hackathon.finservice.Repositories;

import com.hackathon.finservice.Entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, String> {
    Token findByToken(String token);
}
