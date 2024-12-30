package com.hackathon.finservice.Controllers;

import com.hackathon.finservice.DTO.LoginRequest;
import com.hackathon.finservice.DTO.RegisterRequest;
import com.hackathon.finservice.DTO.RegisterResponse;
import com.hackathon.finservice.Security.JWTUtils;
import com.hackathon.finservice.Security.TokenBlacklistService;
import com.hackathon.finservice.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final TokenBlacklistService tokenBlacklistService;

    public UserController(UserService userService, JWTUtils jwtUtils, TokenBlacklistService tokenBlacklistService) {
        this.userService = userService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {

        if (validatePassword(request.getPassword()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validatePassword(request.getPassword()));

        }
        RegisterResponse respuesta = userService.register(request);
        if (respuesta == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists: " + request.getEmail());
        } else if (!respuesta.getEmail().isEmpty() && respuesta.getHashedPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad credentials");
        }
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String token = userService.login(request);
        if (token == null) {
            return ResponseEntity.badRequest().body("Bad credentials");
        }
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(name = "Authorization", required = false) String authHeader) {
        // Validar que el encabezado Authorization esté presente
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Access Denied");
        }

        // Extraer el token
        String token = authHeader.substring(7);

        // Invalidar el token agregándolo a la lista negra
        tokenBlacklistService.addToBlacklist(token);

        return ResponseEntity.ok("Logout exitoso. Token invalidado.");
    }

    private String validatePassword(String password) {
        if (password == null || password.length() < 8) {
            return "Password must be at least 8 characters long";
        }
        if (password.length() > 128) {
            return "Password must be less than 128 characters long";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter";
        }
        if (!password.matches(".*\\d.*")) {
            return "Password must contain at least one digit";
        }
        if (!password.matches(".*[!@#$%^&*].*")) {
            return "Password must contain at least one special character";
        }
        if (password.contains(" ")) {
            return "Password cannot contain whitespace";
        }
        return null;
    }


}


