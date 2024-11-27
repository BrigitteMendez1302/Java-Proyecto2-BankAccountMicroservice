package com.example.bankaccount.service.impl;

import com.example.bankaccount.client.CustomerClient;
import com.example.bankaccount.model.BankAccount;
import com.example.bankaccount.repository.BankAccountRepository;
import com.example.bankaccount.rules.BusinessRule;
import com.example.bankaccount.rules.CheckingAccountWithdrawalRule;
import com.example.bankaccount.rules.DepositRule;
import com.example.bankaccount.rules.SavingsAccountWithdrawalRule;
import com.example.bankaccount.service.BankAccountService;
import com.example.bankaccount.service.CustomerValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * BankAccountServiceImpl provides the implementation of the business logic for managing bank accounts.
 * It performs CRUD operations, deposits, withdrawals, and checks account ownership by customer ID.
 */
@Service
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository; // Repository for accessing bank account data
    private final CustomerValidationService customerValidationService;
    private final Map<BankAccount.AccountType, BusinessRule> withdrawalRules = Map.of(
            BankAccount.AccountType.SAVINGS, new SavingsAccountWithdrawalRule(),
            BankAccount.AccountType.CHECKING, new CheckingAccountWithdrawalRule()
    );

    /**
     * Constructor to initialize the BankAccountServiceImpl with required dependencies.
     *
     * @param bankAccountRepository The repository for bank account data.
     */
    @Autowired
    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository, CustomerValidationService customerValidationService) {
        this.bankAccountRepository = bankAccountRepository;
        this.customerValidationService = customerValidationService;
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
        if (!customerValidationService.isCustomerExists(bankAccount.getCustomerId())) {
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
        if (!customerValidationService.isCustomerExists(customerId)) {
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
        if (!customerValidationService.isCustomerExists(bankAccount.getCustomerId())) {
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
        return bankAccountRepository.findById(id)
                .map(account -> {
                    // Aplicar la regla de depósito
                    BusinessRule depositRule = new DepositRule();
                    depositRule.validate(account, amount);

                    // Actualizar el saldo de la cuenta
                    account.setBalance(account.getBalance().add(amount));
                    return bankAccountRepository.save(account);
                })
                .orElseThrow(() -> new NoSuchElementException("Bank account not found"));
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
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }

        return bankAccountRepository.findById(id)
                .map(account -> {
                    // Seleccionar y aplicar la regla de retiro según el tipo de cuenta
                    BusinessRule rule = withdrawalRules.get(account.getAccountType());
                    if (rule == null) {
                        throw new IllegalArgumentException("No withdrawal rule defined for this account type.");
                    }
                    rule.validate(account, amount);

                    // Actualizar el saldo de la cuenta
                    account.setBalance(account.getBalance().subtract(amount));
                    return bankAccountRepository.save(account);
                })
                .orElseThrow(() -> new NoSuchElementException("Bank account not found"));
    }
}