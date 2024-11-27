package com.example.bankaccount.rules;

import com.example.bankaccount.model.BankAccount;

import java.math.BigDecimal;

public class CheckingAccountWithdrawalRule implements BusinessRule {
    @Override
    public void validate(BankAccount account, BigDecimal amount) {
        BigDecimal newBalance = account.getBalance().subtract(amount);
        if (newBalance.compareTo(new BigDecimal("-500")) < 0) {
            throw new IllegalArgumentException("Checking accounts cannot have a balance below -500.");
        }
    }
}
