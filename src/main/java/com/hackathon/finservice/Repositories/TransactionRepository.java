package com.hackathon.finservice.Repositories;

import com.hackathon.finservice.Entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findBysourceAccountNumber(String accountNumber);
}
