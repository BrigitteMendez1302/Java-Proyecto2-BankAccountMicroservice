package com.example.bankaccount.rules;

import com.example.bankaccount.model.BankAccount;

import java.math.BigDecimal;

public class DepositRule implements BusinessRule {

    @Override
    public void validate(BankAccount account, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
    }
}
