package com.hackathon.finservice.Service;

import com.hackathon.finservice.Entities.Account;
import com.hackathon.finservice.Entities.User;
import com.hackathon.finservice.Repositories.AccountRepository;
import com.hackathon.finservice.Repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public Account getMainAccountByEmail(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    public boolean createAccount(String userEmail, String accountNumber, String accountType, boolean firstAccount) {
        Account account = new Account();
        if (!firstAccount) {
            if (accountRepository.findByAccountNumber(accountNumber) != null) {
                accountNumber = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
                account.setAccountNumber(accountNumber);
            } else {
                return false;
            }
        } else {
            account.setAccountNumber(accountNumber);
        }
        account.setEmail(userEmail);
        account.setAccountType(accountType);
        account.setBalance(0.0);
        accountRepository.save(account);
        return true;
    }

    public String getAccountByIndex(String email, int index) {
        List<Account> accounts = accountRepository.findAccountByEmail(email);
        if (!accounts.isEmpty()) {
            return accounts.get(index).construirRespuesta();
        }
        return null;
    }

    public Account createInvestmentAccount(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        String accountNumber = user.isPresent() ? user.get().getAccountNumber() : "";
        Account existingAccount = accountRepository.findByAccountNumber(accountNumber);
        if (existingAccount != null && "Invest".equals(existingAccount.getAccountType())) {
            return existingAccount; // Si ya existe, retornar la cuenta existente
        }

        Account investAccount = new Account(accountNumber, email, 0.0, "Invest");
        return accountRepository.save(investAccount);
    }
}