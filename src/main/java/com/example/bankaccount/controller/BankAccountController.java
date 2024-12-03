package com.example.bankaccount.controller;

import com.example.bankaccount.dto.BankAccountRequestDto;
import com.example.bankaccount.dto.BankAccountResponseDto;
import com.example.bankaccount.mapper.BankAccountMapper;
import com.example.bankaccount.model.BankAccount;
import com.example.bankaccount.service.BankAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Bank Account", description = "Operaciones sobre cuentas bancarias")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @Autowired
    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @Operation(summary = "Create a new bank account", description = "Creates a new bank account for a customer.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Bank account created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BankAccountResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content)
    })
    @PostMapping
    public ResponseEntity<BankAccountResponseDto> createBankAccount(
            @Valid @RequestBody @Parameter(description = "Bank account creation details", required = true) BankAccountRequestDto requestDto) {
        BankAccount bankAccount = BankAccountMapper.toEntity(requestDto);
        BankAccount createdAccount = bankAccountService.createBankAccount(bankAccount);
        BankAccountResponseDto responseDto = BankAccountMapper.toResponseDto(createdAccount);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Retrieve all bank accounts", description = "Fetches a list of all bank accounts.")
    @ApiResponse(responseCode = "200", description = "List of bank accounts retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BankAccountResponseDto.class)))
    @GetMapping
    public ResponseEntity<List<BankAccountResponseDto>> getAllBankAccounts() {
        List<BankAccount> bankAccounts = bankAccountService.getAllBankAccounts();
        List<BankAccountResponseDto> responseDtos = bankAccounts.stream()
                .map(BankAccountMapper::toResponseDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    @Operation(summary = "Retrieve a bank account by ID", description = "Fetches details of a specific bank account by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bank account details retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BankAccountResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Bank account not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<BankAccountResponseDto> getBankAccountById(
            @PathVariable @Parameter(description = "ID of the bank account", required = true) Long id) {
        BankAccount bankAccount = bankAccountService.getBankAccountById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bank account not found"));
        BankAccountResponseDto responseDto = BankAccountMapper.toResponseDto(bankAccount);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(summary = "Deposit an amount", description = "Deposits a specified amount into a bank account.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deposit successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BankAccountResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid deposit request", content = @Content)
    })
    @PutMapping("/{accountId}/deposit")
    public ResponseEntity<BankAccountResponseDto> deposit(
            @PathVariable @Parameter(description = "ID of the bank account", required = true) Long accountId,
            @RequestParam @Parameter(description = "Amount to deposit", required = true) BigDecimal amount) {
        BankAccount updatedAccount = bankAccountService.deposit(accountId, amount);
        BankAccountResponseDto responseDto = BankAccountMapper.toResponseDto(updatedAccount);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(summary = "Withdraw an amount", description = "Withdraws a specified amount from a bank account.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Withdrawal successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BankAccountResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid withdrawal request", content = @Content)
    })
    @PutMapping("/{accountId}/withdraw")
    public ResponseEntity<BankAccountResponseDto> withdraw(
            @PathVariable @Parameter(description = "ID of the bank account", required = true) Long accountId,
            @RequestParam @Parameter(description = "Amount to withdraw", required = true) BigDecimal amount) {
        BankAccount updatedAccount = bankAccountService.withdraw(accountId, amount);
        BankAccountResponseDto responseDto = BankAccountMapper.toResponseDto(updatedAccount);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(summary = "Delete a bank account", description = "Deletes a bank account by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Bank account deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Bank account not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBankAccount(
            @PathVariable @Parameter(description = "ID of the bank account", required = true) Long id) {
        bankAccountService.deleteBankAccount(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Retrieve accounts for a customer", description = "Fetches all bank accounts for a specific customer.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of bank accounts retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BankAccountResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "No bank accounts found for the customer", content = @Content)
    })
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<BankAccountResponseDto>> getBankAccountsByCustomerId(
            @PathVariable @Parameter(description = "ID of the customer", required = true) Long customerId) {
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
