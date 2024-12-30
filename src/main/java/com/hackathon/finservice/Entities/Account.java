package com.hackathon.finservice.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Account {
    @Id
    private String accountNumber;
    private String email;
    private double balance;
    private String accountType;

    public Account(String accountNumber, String email, double balance, String accountType) {
        this.accountNumber = accountNumber;
        this.email = email;
        this.balance = balance;
        this.accountType = accountType;
    }

    public Account() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String userEmail) {
        this.email = userEmail;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String construirRespuesta() {
        return "{"
                + "\"accountNumber\":\"" + accountNumber + "\","
                + "\"balance\":" + balance + ","
                + "\"accountType\":\"" + accountType + "\""
                + "}";
    }
}