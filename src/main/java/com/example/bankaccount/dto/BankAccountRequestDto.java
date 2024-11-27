package com.example.bankaccount.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for receiving bank account data in HTTP requests.
 */
@Getter
@Setter
@AllArgsConstructor
public class BankAccountRequestDto {

    @NotNull(message = "Customer ID is required.")
    @Positive(message = "Customer ID must be a positive value.")
    private Long customerId;

    @NotNull(message = "Account type is required.")
    private String accountType;

    @NotNull(message = "Balance is required.")
    @DecimalMin(value = "0.0", inclusive = true, message = "Balance must be zero or greater.")
    private Double balance;
}
