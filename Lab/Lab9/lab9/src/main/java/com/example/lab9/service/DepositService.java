package com.example.lab9.service;

import com.example.lab9.model.Account;
import com.example.lab9.model.DepositTransaction;
import com.example.lab9.repository.AccountRepository;
import com.example.lab9.repository.DepositRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepositService {

    private final AccountRepository accountRepository;
    private final DepositRepository depositRepository;

    public DepositService(AccountRepository accountRepository, DepositRepository depositRepository) {
        this.accountRepository = accountRepository;
        this.depositRepository = depositRepository;
    }

    @Transactional
    public void deposit(Long accountId, Double amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));

        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        DepositTransaction transaction = new DepositTransaction(amount, account);
        depositRepository.save(transaction);
    }
}