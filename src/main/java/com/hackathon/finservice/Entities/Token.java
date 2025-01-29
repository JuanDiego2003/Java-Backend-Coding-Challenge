package com.hackathon.finservice.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Token {
    @Id
    private String token;

    public Token() {
    }

    public Token(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
