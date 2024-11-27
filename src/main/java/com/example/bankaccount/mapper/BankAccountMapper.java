package com.example.bankaccount.mapper;

import com.example.bankaccount.dto.BankAccountRequestDto;
import com.example.bankaccount.dto.BankAccountResponseDto;
import com.example.bankaccount.model.BankAccount;

import java.math.BigDecimal;

public class BankAccountMapper {

    public static BankAccount toEntity(BankAccountRequestDto dto) {
        return new BankAccount(
                BankAccount.AccountType.valueOf(dto.getAccountType()),
                BigDecimal.valueOf(dto.getBalance()),
                dto.getCustomerId()
        );
    }

    public static BankAccountResponseDto toResponseDto(BankAccount account) {
        return new BankAccountResponseDto(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance().doubleValue(),
                account.getAccountType().toString(),
                account.getCustomerId()
        );
    }
}
