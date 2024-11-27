package com.example.bankaccount.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for sending bank account data in HTTP responses.
 */
@Getter
@Setter
@AllArgsConstructor
public class BankAccountResponseDto {

    private Long id;
    private String accountNumber;
    private Double balance;
    private String accountType;
    private Long customerId;
}
