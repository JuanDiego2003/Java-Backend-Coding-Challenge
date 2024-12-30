package com.hackathon.finservice.Controllers;

import com.hackathon.finservice.DTO.AccountRequest;
import com.hackathon.finservice.DTO.TransactionRequest;
import com.hackathon.finservice.Security.JWTUtils;
import com.hackathon.finservice.Service.AccountService;
import com.hackathon.finservice.Service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    private final JWTUtils jwtUtils;
    private final AccountService accountService;
    private final TransactionService transactionService;


    public AccountController(JWTUtils jwtUtils, AccountService accountService, TransactionService transactionService) {
        this.jwtUtils = jwtUtils;
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@RequestHeader("Authorization") String token, @RequestBody AccountRequest accountRequest) {
        accountService.createAccount(jwtUtils.extractEmail(token.replace("Bearer ", "")), accountRequest.getAccountNumber(), accountRequest.getAccountType(), false);
        return ResponseEntity.ok("New account added successfully for user");
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestHeader("Authorization") String token, @RequestBody TransactionRequest transactionRequest) throws AccountNotFoundException {
        String userEmail = jwtUtils.extractEmail(token.replace("Bearer ", ""));
        // Supongamos que el usuario siempre deposita en la cuenta principal
        String respuesta = transactionService.deposit(userEmail, transactionRequest.getAmount());
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestHeader("Authorization") String token, @RequestBody TransactionRequest transactionRequest) throws AccountNotFoundException {
        String userEmail = jwtUtils.extractEmail(token.replace("Bearer ", ""));
        String respuesta = transactionService.withdraw(userEmail, transactionRequest.getAmount());
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> Transfer(@RequestHeader("Authorization") String token, @RequestBody TransactionRequest transactionRequest) throws AccountNotFoundException {
        String userEmail = jwtUtils.extractEmail(token.replace("Bearer ", ""));
        String respuesta = transactionService.transfer(userEmail, transactionRequest.getTargetAccountNumber(), transactionRequest.getAmount());

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(@RequestHeader("Authorization") String token) {
        String userEmail = jwtUtils.extractEmail(token.replace("Bearer ", ""));
        return ResponseEntity.ok(transactionService.getTransactionHistory(userEmail));
    }

}
