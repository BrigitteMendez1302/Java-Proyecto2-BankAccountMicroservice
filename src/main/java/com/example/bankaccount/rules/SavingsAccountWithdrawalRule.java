package com.example.bankaccount.rules;

import com.example.bankaccount.model.BankAccount;

import java.math.BigDecimal;

public class SavingsAccountWithdrawalRule implements BusinessRule {
    @Override
    public void validate(BankAccount account, BigDecimal amount) {
        BigDecimal newBalance = account.getBalance().subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Savings accounts cannot have a negative balance.");
        }
    }
}
