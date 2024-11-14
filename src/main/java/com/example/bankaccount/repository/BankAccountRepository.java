package com.example.bankaccount.repository;

import com.example.bankaccount.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * BankAccountRepository is the interface for CRUD operations on BankAccount entities.
 * This repository extends JpaRepository to provide methods to perform database
 * operations on BankAccount data.
 *
 * Functionalities provided:
 * - Create, read, update, and delete (CRUD) bank accounts.
 * - Find bank accounts by customer ID.
 * - Check the existence of bank accounts by customer ID.
 */
@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    /**
     * Finds all bank accounts associated with a specific customer ID.
     *
     * @param customerId The ID of the customer.
     * @return A list of BankAccount entities for the specified customer.
     */
    List<BankAccount> findByCustomerId(Long customerId);

    /**
     * Finds a bank account by its ID and customer ID, useful for validation and specific queries.
     *
     * @param id The ID of the bank account.
     * @param customerId The ID of the customer who owns the account.
     * @return An Optional containing the BankAccount if found, or empty if not found.
     */
    Optional<BankAccount> findByIdAndCustomerId(Long id, Long customerId);

    /**
     * Checks the existence of bank accounts for a specific customer ID.
     * This can be useful for verifying if a customer has any bank accounts.
     *
     * @param customerId The ID of the customer.
     * @return True if one or more bank accounts exist for the customer, false otherwise.
     */
    boolean existsByCustomerId(Long customerId);

    /**
     * Checks if an account with a specific account number exists.
     *
     * @param accountNumber The unique account number to check.
     * @return True if an account with the specified account number exists, false otherwise.
     */
    boolean existsByAccountNumber(String accountNumber);
}