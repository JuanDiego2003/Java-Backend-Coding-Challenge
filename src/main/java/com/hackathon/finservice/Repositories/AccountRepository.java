package com.hackathon.finservice.Repositories;

import com.hackathon.finservice.Entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findAccountByEmail(String email);
    Account findByAccountNumber(String accountNumber);
    List<Account> findByAccountType(String invest);
}
