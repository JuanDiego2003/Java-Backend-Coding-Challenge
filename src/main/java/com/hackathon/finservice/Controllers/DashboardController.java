package com.hackathon.finservice.Controllers;

import com.hackathon.finservice.Entities.User;
import com.hackathon.finservice.Security.JWTUtils;
import com.hackathon.finservice.Service.AccountService;
import com.hackathon.finservice.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final UserService userService;
    private final JWTUtils jwtUtils;
    private final AccountService accountService;

    public DashboardController(UserService userService, JWTUtils jwtUtils, AccountService accountService) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.accountService = accountService;
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String token) {
        String userEmail = jwtUtils.extractEmail(token.replace("Bearer ", ""));
        User user = userService.getUserByEmail(userEmail);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Access Denied");
        }
        return ResponseEntity.ok(userService.buildUserInfoResponse(user));
    }

    @GetMapping("/account")
    public ResponseEntity<?> getAccountInfo(@RequestHeader("Authorization") String token) {
        User user = userService.getUserByEmail(jwtUtils.extractEmail(token.replace("Bearer ", "")));
        String respuesta = accountService.getMainAccountByEmail(user.getAccountNumber()).construirRespuesta();
        if (respuesta == null || respuesta.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Access Denied");
        }
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/account/{index}")
    public ResponseEntity<?> getAccountByIndex(@RequestHeader("Authorization") String token, @PathVariable int index) {
        return ResponseEntity.ok(Objects.requireNonNullElse(accountService.getAccountByIndex(jwtUtils.extractEmail(token.replace("Bearer ", "")), index), "Account not found, index out of range"));
    }

}
