package com.example.bankaccount.service.impl;

import com.example.bankaccount.model.BankAccount;
import com.example.bankaccount.repository.BankAccountRepository;
import com.example.bankaccount.service.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * BankAccountServiceImpl provides the implementation of the business logic for managing bank accounts.
 * It performs CRUD operations, deposits, withdrawals, and checks account ownership by customer ID.
 */
@Service
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    @Autowired
    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    @Transactional
    public BankAccount createBankAccount(BankAccount bankAccount) {
        return bankAccountRepository.save(bankAccount);
    }

    @Override
    public List<BankAccount> getAllBankAccounts() {
        return bankAccountRepository.findAll();
    }

    @Override
    public Optional<BankAccount> getBankAccountById(Long id) {
        return bankAccountRepository.findById(id);
    }

    @Override
    public List<BankAccount> getBankAccountsByCustomerId(Long customerId) {
        return bankAccountRepository.findByCustomerId(customerId);
    }

    @Override
    @Transactional
    public BankAccount updateBankAccount(Long id, BankAccount bankAccount) {
        return bankAccountRepository.findById(id)
                .map(existingAccount -> {
                    existingAccount.setAccountType(bankAccount.getAccountType());
                    existingAccount.setBalance(bankAccount.getBalance());
                    existingAccount.setCustomerId(bankAccount.getCustomerId());
                    return bankAccountRepository.save(existingAccount);
                })
                .orElseThrow(() -> new NoSuchElementException("Bank account not found"));
    }

    @Override
    @Transactional
    public boolean deleteBankAccount(Long id) {
        return bankAccountRepository.findById(id)
                .map(account -> {
                    bankAccountRepository.delete(account);
                    return true;
                })
                .orElse(false);
    }

    @Override
    @Transactional
    public BankAccount deposit(Long id, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        return bankAccountRepository.findById(id)
                .map(account -> {
                    account.setBalance(account.getBalance().add(amount));
                    return bankAccountRepository.save(account);
                })
                .orElseThrow(() -> new NoSuchElementException("Bank account not found"));
    }

    @Override
    @Transactional
    public BankAccount withdraw(Long id, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }

        return bankAccountRepository.findById(id)
                .map(account -> {
                    BigDecimal newBalance = account.getBalance().subtract(amount);

                    // Apply rules based on account type
                    if (account.getAccountType() == BankAccount.AccountType.SAVINGS && newBalance.compareTo(BigDecimal.ZERO) < 0) {
                        throw new IllegalArgumentException("Savings accounts cannot have a negative balance.");
                    } else if (account.getAccountType() == BankAccount.AccountType.CHECKING && newBalance.compareTo(new BigDecimal("-500")) < 0) {
                        throw new IllegalArgumentException("Checking accounts cannot have a balance below -500.");
                    }

                    account.setBalance(newBalance);
                    return bankAccountRepository.save(account);
                })
                .orElseThrow(() -> new NoSuchElementException("Bank account not found"));
    }

    @Override
    public boolean customerHasBankAccounts(Long customerId) {
        return bankAccountRepository.existsByCustomerId(customerId);
    }
}
