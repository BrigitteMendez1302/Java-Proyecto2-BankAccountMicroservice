package com.example.bankaccount.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Represents a bank account entity with attributes such as account number, balance, account type, and customer ID.
 * The account type can be either SAVINGS or CHECKING, with different overdraft limits and balance rules.
 */
@Getter
@ToString
@Entity
@Table(name = "bank_accounts")
public class BankAccount {

    public enum AccountType {
        SAVINGS, CHECKING
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Account number is required.")
    @Column(name = "account_number", nullable = false, unique = true, updatable = false)
    private String accountNumber;

    @NotNull(message = "Balance is required.")
    @DecimalMin(value = "0.0", inclusive = true, message = "Initial balance must be zero or greater.")
    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @NotNull(message = "Account type is required.")
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    @NotNull(message = "Customer ID is required.")
    @Positive(message = "Customer ID must be a positive value.")
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    /**
     * Default constructor required by JPA.
     */
    public BankAccount() {
        this.accountNumber = generateAccountNumber();
        this.balance = BigDecimal.ZERO;
    }

    /**
     * Constructs a bank account with specified account type, initial balance, and associated customer ID.
     *
     * @param accountType The type of the bank account (SAVINGS or CHECKING).
     * @param balance     The initial balance of the bank account.
     * @param customerId  The ID of the customer associated with the bank account.
     */
    public BankAccount(AccountType accountType, BigDecimal balance, Long customerId) {
        this.accountNumber = generateAccountNumber();
        this.accountType = accountType;
        this.balance = balance;
        this.customerId = customerId;
    }

    // Getters and Setters

    /**
     * Sets the balance of the bank account. For SAVINGS accounts, the balance cannot go below zero.
     * For CHECKING accounts, an overdraft limit of -500 is allowed.
     *
     * @param balance The new balance.
     * @throws IllegalArgumentException if the balance violates account type restrictions.
     */
    public void setBalance(BigDecimal balance) {
        if (this.accountType == AccountType.SAVINGS && balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Savings accounts cannot have a negative balance.");
        }
        if (this.accountType == AccountType.CHECKING && balance.compareTo(new BigDecimal("-500")) < 0) {
            throw new IllegalArgumentException("Checking accounts cannot have a balance below -500.");
        }
        this.balance = balance;
    }

    /**
     * Sets the type of the bank account.
     *
     * @param accountType The type of the bank account.
     * @throws IllegalArgumentException if accountType is null.
     */
    public void setAccountType(AccountType accountType) {
        if (accountType == null) {
            throw new IllegalArgumentException("Account type is required.");
        }
        this.accountType = accountType;
    }

    /**
     * Sets the customer ID associated with the bank account.
     *
     * @param customerId The ID of the customer, must be a positive value.
     * @throws IllegalArgumentException if customerId is not positive.
     */
    public void setCustomerId(Long customerId) {
        if (customerId == null || customerId <= 0) {
            throw new IllegalArgumentException("Customer ID must be a positive value.");
        }
        this.customerId = customerId;
    }

    /**
     * Generates a unique account number using UUID.
     *
     * @return A unique account number.
     */
    private String generateAccountNumber() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
}