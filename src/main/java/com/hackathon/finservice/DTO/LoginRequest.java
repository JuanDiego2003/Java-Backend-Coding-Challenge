package com.hackathon.finservice.DTO;
import lombok.Data;

@Data
public class LoginRequest {
    private String identifier;
    private String password;

    public String getEmail() {
        return identifier;
    }

    public void setEmail(String email) {
        this.identifier = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}