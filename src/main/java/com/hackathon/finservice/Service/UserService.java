package com.hackathon.finservice.Service;

import com.hackathon.finservice.DTO.LoginRequest;
import com.hackathon.finservice.DTO.RegisterRequest;
import com.hackathon.finservice.DTO.RegisterResponse;
import com.hackathon.finservice.Entities.User;
import com.hackathon.finservice.Repositories.UserRepository;
import com.hackathon.finservice.Security.JWTUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    private final AccountService accountService;

    public UserService(UserRepository userRepository, JWTUtils jwtUtils, AccountService accountService) {
        this.userRepository = userRepository;
        this.accountService = accountService;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtUtils = jwtUtils;
    }

    // Registro de usuario
    public RegisterResponse register(RegisterRequest request) {
        RegisterResponse response = new RegisterResponse();

        // Crear usuario
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // Respuesta
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setAccountType("Main");

        // Verificar si el correo ya está registrado
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            response = null;
            return response;
        }

        // Validación de la contraseña (con reglas personalizadas si es necesario)
        if (!isValidPassword(request.getPassword())) {
            return response;
        }

        // Cifrado de la contraseña
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Generación del número de cuenta
        String accountNumber;
        do {
            accountNumber = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        } while (userRepository.findByAccountNumber(accountNumber).isPresent());

        //Completar usuario
        user.setPassword(hashedPassword);
        user.setAccountNumber(accountNumber);

        // Guardar usuario en la base de datos
        userRepository.save(user);

        //Completar respuesta
        response.setAccountNumber(user.getAccountNumber());
        response.setHashedPassword(hashedPassword);
        accountService.createAccount(user.getEmail(), user.getAccountNumber(), user.getAccountType(), true);
        return response;
    }

    private boolean isValidPassword(String password) {
        // Ejemplo de validación de contraseña: al menos 8 caracteres, 1 mayúscula, 1 número, 1 carácter especial
        return password != null && password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,}$");
    }

    // Login de usuario
    public String login(LoginRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        String respuesta;

        if (user.isEmpty() || !passwordEncoder.matches(request.getPassword(), (user.isPresent() ? user.get().getPassword() : ""))) {
            return null;
        }
        respuesta = "{\"token\": \"" + jwtUtils.generateToken(user.get().getEmail()) + "\"}";
        return respuesta;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> null);
    }

    public RegisterResponse buildUserInfoResponse(User user) {
        RegisterResponse response = new RegisterResponse();
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setAccountNumber(user.getAccountNumber());
        response.setAccountType(user.getAccountType());
        response.setHashedPassword(user.getPassword()); // Esto es opcional
        return response;
    }

}