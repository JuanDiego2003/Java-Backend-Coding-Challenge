package com.hackathon.finservice.Service;

import com.hackathon.finservice.Entities.Account;
import com.hackathon.finservice.Repositories.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterestService {

    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(InterestService.class);

    private final AccountRepository accountRepository;

    public InterestService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // Programar la ejecución cada 10 segundos
    @Scheduled(fixedRate = 10000) // 10 segundos
    public void applyInterestToInvestmentAccounts() {
        List<Account> investmentAccounts = accountRepository.findByAccountType("Invest");
        for (Account account : investmentAccounts) {
            double newBalance = account.getBalance() * 1.1; // Aplicar 10% de interés
            account.setBalance(newBalance);
            accountRepository.save(account);
            logger.info("Applied 10% interest to account {}. New balance: {}", account.getAccountNumber(), account.getBalance());
        }
    }
}
