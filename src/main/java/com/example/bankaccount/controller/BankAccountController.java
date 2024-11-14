package com.example.bankaccount.controller;

import com.example.bankaccount.model.BankAccount;
import com.example.bankaccount.service.BankAccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * BankAccountController handles HTTP requests for managing bank accounts.
 * It provides endpoints for creating, retrieving, depositing, withdrawing, and deleting bank accounts.
 */
@RestController
@RequestMapping("/api/accounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @Autowired
    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    /**
     * Endpoint to create a new bank account for a customer.
     *
     * @param bankAccount The bank account to be created.
     * @return The created bank account with status 201 Created.
     */
    @PostMapping
    public ResponseEntity<BankAccount> createBankAccount(@Valid @RequestBody BankAccount bankAccount) {
        BankAccount createdAccount = bankAccountService.createBankAccount(bankAccount);
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }

    /**
     * Endpoint to retrieve all bank accounts.
     *
     * @return A list of all bank accounts.
     */
    @GetMapping
    public ResponseEntity<List<BankAccount>> getAllBankAccounts() {
        List<BankAccount> bankAccounts = bankAccountService.getAllBankAccounts();
        return new ResponseEntity<>(bankAccounts, HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve a bank account by ID.
     *
     * @param id The ID of the bank account to retrieve.
     * @return The bank account if found, or 404 Not Found if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BankAccount> getBankAccountById(@PathVariable Long id) {
        BankAccount bankAccount = bankAccountService.getBankAccountById(id)
                .orElseThrow(() -> new NoSuchElementException("Bank account not found"));
        return new ResponseEntity<>(bankAccount, HttpStatus.OK);
    }

    /**
     * Endpoint to deposit an amount into a bank account.
     *
     * @param accountId The ID of the bank account to deposit into.
     * @param amount   The amount to deposit.
     * @return The updated bank account with the new balance.
     */
    @PutMapping("/{accountId}/deposit")
    public ResponseEntity<BankAccount> deposit(@PathVariable Long accountId, @RequestParam BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        BankAccount updatedAccount = bankAccountService.deposit(accountId, amount);
        return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
    }

    /**
     * Endpoint to withdraw an amount from a bank account.
     *
     * @param accountId The ID of the bank account to withdraw from.
     * @param amount   The amount to withdraw.
     * @return The updated bank account with the new balance.
     */
    @PutMapping("/{accountId}/withdraw")
    public ResponseEntity<BankAccount> withdraw(@PathVariable Long accountId, @RequestParam BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        BankAccount updatedAccount = bankAccountService.withdraw(accountId, amount);
        return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
    }

    /**
     * Endpoint to delete a bank account by ID.
     *
     * @param id The ID of the bank account to delete.
     * @return 204 No Content if successful, or 404 Not Found if the account does not exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBankAccount(@PathVariable Long id) {
        boolean isDeleted = bankAccountService.deleteBankAccount(id);
        return isDeleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
