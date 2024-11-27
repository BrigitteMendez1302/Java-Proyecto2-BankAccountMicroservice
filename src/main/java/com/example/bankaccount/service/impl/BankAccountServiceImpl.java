package com.example.bankaccount.service.impl;

import com.example.bankaccount.client.CustomerClient;
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

    private final BankAccountRepository bankAccountRepository; // Repository for accessing bank account data
    private final CustomerClient customerClient; // Client for interacting with the Customer microservice

    /**
     * Constructor to initialize the BankAccountServiceImpl with required dependencies.
     *
     * @param bankAccountRepository The repository for bank account data.
     * @param customerClient        The client for verifying customer existence.
     */
    @Autowired
    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository, CustomerClient customerClient) {
        this.bankAccountRepository = bankAccountRepository;
        this.customerClient = customerClient;
    }

    /**
     * Creates a new bank account for a customer.
     *
     * @param bankAccount The bank account to be created.
     * @return The created bank account.
     * @throws IllegalArgumentException If the customer does not exist.
     */
    @Override
    @Transactional
    public BankAccount createBankAccount(BankAccount bankAccount) {
        if (!customerClient.isCustomerExists(bankAccount.getCustomerId())) {
            throw new IllegalArgumentException("Customer does not exist."); // Validate customer existence
        }
        return bankAccountRepository.save(bankAccount); // Save and return the created bank account
    }

    /**
     * Retrieves all bank accounts.
     *
     * @return A list of all bank accounts.
     */
    @Override
    public List<BankAccount> getAllBankAccounts() {
        return bankAccountRepository.findAll(); // Fetch all bank accounts from the database
    }

    /**
     * Retrieves a bank account by its ID.
     *
     * @param id The ID of the bank account.
     * @return An Optional containing the bank account if found.
     */
    @Override
    public Optional<BankAccount> getBankAccountById(Long id) {
        return bankAccountRepository.findById(id); // Fetch a bank account by its ID
    }

    /**
     * Retrieves all bank accounts for a specific customer.
     *
     * @param customerId The ID of the customer.
     * @return A list of bank accounts for the customer.
     * @throws IllegalArgumentException If the customer does not exist.
     */
    @Override
    public List<BankAccount> getBankAccountsByCustomerId(Long customerId) {
        if (!customerClient.isCustomerExists(customerId)) {
            throw new IllegalArgumentException("Customer does not exist."); // Validate customer existence
        }
        return bankAccountRepository.findByCustomerId(customerId); // Fetch bank accounts by customer ID
    }

    /**
     * Updates an existing bank account.
     *
     * @param id          The ID of the bank account to update.
     * @param bankAccount The updated bank account details.
     * @return The updated bank account.
     * @throws IllegalArgumentException If the customer does not exist.
     * @throws NoSuchElementException   If the bank account does not exist.
     */
    @Override
    @Transactional
    public BankAccount updateBankAccount(Long id, BankAccount bankAccount) {
        if (!customerClient.isCustomerExists(bankAccount.getCustomerId())) {
            throw new IllegalArgumentException("Customer does not exist."); // Validate customer existence
        }
        return bankAccountRepository.findById(id)
                .map(existingAccount -> {
                    existingAccount.setAccountType(bankAccount.getAccountType()); // Update account type
                    existingAccount.setBalance(bankAccount.getBalance()); // Update balance
                    existingAccount.setCustomerId(bankAccount.getCustomerId()); // Update customer ID
                    return bankAccountRepository.save(existingAccount); // Save updated account
                })
                .orElseThrow(() -> new NoSuchElementException("Bank account not found")); // Handle account not found
    }

    /**
     * Deletes a bank account by its ID.
     *
     * @param id The ID of the bank account to delete.
     * @return true if the account was deleted; false otherwise.
     */
    @Override
    @Transactional
    public boolean deleteBankAccount(Long id) {
        return bankAccountRepository.findById(id)
                .map(account -> {
                    bankAccountRepository.delete(account); // Delete the account
                    return true; // Return success
                })
                .orElse(false); // Return false if the account does not exist
    }

    /**
     * Deposits an amount into a bank account.
     *
     * @param id     The ID of the bank account.
     * @param amount The amount to deposit.
     * @return The updated bank account with the new balance.
     * @throws IllegalArgumentException If the amount is not positive or the account does not exist.
     */
    @Override
    @Transactional
    public BankAccount deposit(Long id, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive."); // Validate deposit amount
        }
        return bankAccountRepository.findById(id)
                .map(account -> {
                    account.setBalance(account.getBalance().add(amount)); // Add amount to balance
                    return bankAccountRepository.save(account); // Save updated account
                })
                .orElseThrow(() -> new NoSuchElementException("Bank account not found")); // Handle account not found
    }

    /**
     * Withdraws an amount from a bank account.
     *
     * @param id     The ID of the bank account.
     * @param amount The amount to withdraw.
     * @return The updated bank account with the new balance.
     * @throws IllegalArgumentException If the amount is not positive or violates balance rules.
     * @throws NoSuchElementException   If the bank account does not exist.
     */
    @Override
    @Transactional
    public BankAccount withdraw(Long id, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive."); // Validate withdrawal amount
        }

        return bankAccountRepository.findById(id)
                .map(account -> {
                    BigDecimal newBalance = account.getBalance().subtract(amount); // Calculate new balance

                    // Apply rules based on account type
                    if (account.getAccountType() == BankAccount.AccountType.SAVINGS && newBalance.compareTo(BigDecimal.ZERO) < 0) {
                        throw new IllegalArgumentException("Savings accounts cannot have a negative balance."); // Rule for savings accounts
                    } else if (account.getAccountType() == BankAccount.AccountType.CHECKING && newBalance.compareTo(new BigDecimal("-500")) < 0) {
                        throw new IllegalArgumentException("Checking accounts cannot have a balance below -500."); // Rule for checking accounts
                    }

                    account.setBalance(newBalance); // Update balance
                    return bankAccountRepository.save(account); // Save updated account
                })
                .orElseThrow(() -> new NoSuchElementException("Bank account not found")); // Handle account not found
    }

    /**
     * Checks if a customer owns any bank accounts.
     *
     * @param customerId The ID of the customer.
     * @return true if the customer owns any bank accounts; false otherwise.
     */
    @Override
    public boolean customerHasBankAccounts(Long customerId) {
        return bankAccountRepository.existsByCustomerId(customerId); // Check for existing accounts
    }
}