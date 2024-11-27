package com.example.bankaccount.controller;

import com.example.bankaccount.dto.BankAccountRequestDto;
import com.example.bankaccount.dto.BankAccountResponseDto;
import com.example.bankaccount.mapper.BankAccountMapper;
import com.example.bankaccount.model.BankAccount;
import com.example.bankaccount.service.BankAccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * BankAccountController handles HTTP requests for managing bank accounts.
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
     * Create a new bank account for a customer.
     */
    @PostMapping
    public ResponseEntity<BankAccountResponseDto> createBankAccount(@Valid @RequestBody BankAccountRequestDto requestDto) {
        BankAccount bankAccount = BankAccountMapper.toEntity(requestDto);
        BankAccount createdAccount = bankAccountService.createBankAccount(bankAccount);
        BankAccountResponseDto responseDto = BankAccountMapper.toResponseDto(createdAccount);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * Retrieve all bank accounts.
     */
    @GetMapping
    public ResponseEntity<List<BankAccountResponseDto>> getAllBankAccounts() {
        List<BankAccount> bankAccounts = bankAccountService.getAllBankAccounts();
        List<BankAccountResponseDto> responseDtos = bankAccounts.stream()
                .map(BankAccountMapper::toResponseDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    /**
     * Retrieve a bank account by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BankAccountResponseDto> getBankAccountById(@PathVariable Long id) {
        BankAccount bankAccount = bankAccountService.getBankAccountById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bank account not found"));
        BankAccountResponseDto responseDto = BankAccountMapper.toResponseDto(bankAccount);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * Deposit an amount into a bank account.
     */
    @PutMapping("/{accountId}/deposit")
    public ResponseEntity<BankAccountResponseDto> deposit(@PathVariable Long accountId, @RequestParam BigDecimal amount) {
        BankAccount updatedAccount = bankAccountService.deposit(accountId, amount);
        BankAccountResponseDto responseDto = BankAccountMapper.toResponseDto(updatedAccount);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * Withdraw an amount from a bank account.
     */
    @PutMapping("/{accountId}/withdraw")
    public ResponseEntity<BankAccountResponseDto> withdraw(@PathVariable Long accountId, @RequestParam BigDecimal amount) {
        BankAccount updatedAccount = bankAccountService.withdraw(accountId, amount);
        BankAccountResponseDto responseDto = BankAccountMapper.toResponseDto(updatedAccount);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * Delete a bank account by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBankAccount(@PathVariable Long id) {
        bankAccountService.deleteBankAccount(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Retrieve all bank accounts for a specific customer.
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<BankAccountResponseDto>> getBankAccountsByCustomerId(@PathVariable Long customerId) {
        List<BankAccount> bankAccounts = bankAccountService.getBankAccountsByCustomerId(customerId);
        List<BankAccountResponseDto> responseDtos = bankAccounts.stream()
                .map(BankAccountMapper::toResponseDto)
                .collect(Collectors.toList());

        if (responseDtos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }
}
