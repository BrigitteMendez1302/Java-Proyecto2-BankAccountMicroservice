package com.example.bankaccount.service;

import com.example.bankaccount.model.BankAccount;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * BankAccountService defines the main business operations for managing bank accounts.
 * It provides methods to create, retrieve, update, and delete bank accounts, as well as
 * operations such as depositing and withdrawing funds, and retrieving accounts by customer ID.
 */
public interface BankAccountService {

    /**
     * Creates a new bank account for a customer.
     *
     * @param bankAccount The bank account to be created.
     * @return The created BankAccount.
     */
    BankAccount createBankAccount(BankAccount bankAccount);

    /**
     * Retrieves all bank accounts.
     *
     * @return A list of all bank accounts.
     */
    List<BankAccount> getAllBankAccounts();

    /**
     * Retrieves a bank account by its ID.
     *
     * @param id The ID of the bank account to retrieve.
     * @return An Optional containing the bank account if found, or empty if not found.
     */
    Optional<BankAccount> getBankAccountById(Long id);

    /**
     * Retrieves all bank accounts for a specific customer.
     *
     * @param customerId The ID of the customer whose accounts are being retrieved.
     * @return A list of bank accounts associated with the given customer ID.
     */
    List<BankAccount> getBankAccountsByCustomerId(Long customerId);

    /**
     * Updates the details of a bank account.
     *
     * @param id          The ID of the bank account to update.
     * @param bankAccount The updated bank account details.
     * @return The updated BankAccount.
     * @throws IllegalArgumentException if the bank account does not exist.
     */
    BankAccount updateBankAccount(Long id, BankAccount bankAccount);

    /**
     * Deletes a bank account by its ID.
     *
     * @param id The ID of the bank account to delete.
     * @return True if the account was successfully deleted, false otherwise.
     */
    boolean deleteBankAccount(Long id);

    /**
     * Deposits an amount into a bank account.
     *
     * @param id     The ID of the bank account to deposit into.
     * @param amount The amount to deposit, must be positive.
     * @return The updated BankAccount with the new balance.
     * @throws IllegalArgumentException if the amount is not positive.
     */
    BankAccount deposit(Long id, BigDecimal amount);

    /**
     * Withdraws an amount from a bank account.
     *
     * @param id     The ID of the bank account to withdraw from.
     * @param amount The amount to withdraw, must be positive.
     * @return The updated BankAccount with the new balance.
     * @throws IllegalArgumentException if the balance would go below allowed limits.
     */
    BankAccount withdraw(Long id, BigDecimal amount);

    /**
     * Checks if a customer has at least one bank account.
     *
     * @param customerId The ID of the customer to check.
     * @return True if the customer has one or more bank accounts, false otherwise.
     */
    boolean customerHasBankAccounts(Long customerId);
}