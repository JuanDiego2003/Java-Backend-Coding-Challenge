package com.hackathon.finservice.Service;

import com.hackathon.finservice.Entities.*;
import com.hackathon.finservice.Repositories.AccountRepository;
import com.hackathon.finservice.Repositories.TransactionRepository;
import com.hackathon.finservice.Repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final InterestService interestService;

    public TransactionService(UserRepository userRepository, TransactionRepository transactionRepository, AccountRepository accountRepository, InterestService interestService) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.interestService = interestService;
    }

    public String deposit(String email, double amount) {
        Optional<User> user = userRepository.findByEmail(email);
        String accountNumber = user.map(User::getAccountNumber).orElse(null);
        Account account = accountRepository.findByAccountNumber(accountNumber);

        double fee = amount > 50000 ? amount * 0.02 : 0;
        double netAmount = amount - fee;

        account.setBalance(account.getBalance() + netAmount);
        accountRepository.save(account);

        Transaction transaction = new Transaction();

        transaction(transaction, accountNumber, amount, fee, TransactionStatus.PENDING.name(), TransactionStatus.PENDING.name());
        transaction.setTransactionType(TransactionType.CASH_DEPOSIT.name());
        transaction.setTransactionDate(new Date());
        transactionRepository.save(transaction);

        return """
                {
                    "msg": "Cash deposited successfully"
                }""";
    }


    public String withdraw(String email, double amount) {
        Optional<User> user = userRepository.findByEmail(email);
        String accountNumber = user.map(User::getAccountNumber).orElse(null);
        Account account = accountRepository.findByAccountNumber(accountNumber);

        double fee = amount > 10000 ? amount * 0.01 : 0;
        double totalDeduction = amount + fee;

        if (account.getBalance() < totalDeduction) {
            return ("Insufficient balance");
        }

        account.setBalance(account.getBalance() - totalDeduction);
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction(transaction, accountNumber, amount, fee, TransactionStatus.PENDING.name(), TransactionStatus.PENDING.name());
        transaction.setTransactionType(TransactionType.CASH_WITHDRAWAL.name());
        transaction.setTransactionDate(new Date());
        transactionRepository.save(transaction);

        return """
                {
                    "msg": "Cash withdrawn successfully"
                }""";
    }

    public String transfer(String email, String targetAccountNumber, double amount) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return "Error";
        }
        String sourceAccountNumber = user.get().getAccountNumber();
        Account sourceAccount = accountRepository.findByAccountNumber(sourceAccountNumber);
        if (sourceAccount == null) {
            return "Source account not found";
        }
        Account targetAccount = accountRepository.findByAccountNumber(targetAccountNumber);
        if (targetAccount == null) {
            return "Target account not found";
        }
        if (sourceAccount.getBalance() < amount) {
            return "Insufficient balance";
        }

        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
        targetAccount.setBalance(targetAccount.getBalance() + amount);

        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);

        // Crear la transacción para la cuenta de origen
        Transaction sourceTransaction = new Transaction();
        sourceTransaction.setSourceAccountNumber(sourceAccountNumber);
        sourceTransaction.setTargetAccountNumber(targetAccountNumber);
        sourceTransaction.setAmount(amount);
        sourceTransaction.setTransactionType(TransactionType.CASH_TRANSFER.name());
        sourceTransaction.setTransactionStatus(TransactionStatus.PENDING.name());
        sourceTransaction.setTransactionDate(new Date());

        // Crear la transacción para la cuenta de destino
        Transaction targetTransaction = new Transaction();
        targetTransaction.setSourceAccountNumber(targetAccountNumber);
        targetTransaction.setTargetAccountNumber(sourceAccountNumber);
        targetTransaction.setAmount(amount);
        targetTransaction.setTransactionType(TransactionType.CASH_TRANSFER.name());
        targetTransaction.setTransactionStatus(TransactionStatus.PENDING.name());
        targetTransaction.setTransactionDate(new Date());

        if (amount > 80000) {
            sourceTransaction.setTransactionStatus(TransactionStatus.FRAUD.name());
            targetTransaction.setTransactionStatus(TransactionStatus.FRAUD.name());
        }

        transactionRepository.save(sourceTransaction);
        transactionRepository.save(targetTransaction);


        return """
                {
                    "msg": "Fund transferred successfully"
                }""";
    }

    private void transaction(Transaction transaction, String accountNumber, double amount, double fee, String name, String named) {
        transaction.setSourceAccountNumber(accountNumber);
        transaction.setAmount(amount);
        transaction.setTargetAccountNumber("N/A");
    }
    public List<Transaction> getTransactionHistory(String email ) {
        Optional<User> user = userRepository.findByEmail(email);
        List<Transaction> transactions;
        if (user.isPresent()) {
            transactions = transactionRepository.findBysourceAccountNumber(user.get().getAccountNumber());
        }else {
            return null;
        }
        return transactions.reversed();
    }
    public String transferToInvestmentAccount(double amount, String targetAccountNumber) {
        Account targetAccount = accountRepository.findByAccountNumber(targetAccountNumber);

        if (targetAccount == null) {
            return "Account not found";
        }

        if ("Invest".equals(targetAccount.getAccountType())) {
            // Agregar el monto a la cuenta de inversión
            double newBalance = targetAccount.getBalance() + amount;
            targetAccount.setBalance(newBalance);
            accountRepository.save(targetAccount);

            // Aplicar interés automáticamente si es necesario
            interestService.applyInterestToInvestmentAccounts();

            return "Transfer to investment account successful. New balance: " + newBalance;
        }

        return "Target account is not an investment account.";
    }
}