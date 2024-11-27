package com.example.bankaccount.rules;

import com.example.bankaccount.model.BankAccount;

import java.math.BigDecimal;

public interface BusinessRule {
    void validate(BankAccount account, BigDecimal amount);
}
