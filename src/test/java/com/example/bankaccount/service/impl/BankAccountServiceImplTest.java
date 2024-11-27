package com.example.bankaccount.service.impl;

import com.example.bankaccount.model.BankAccount;
import com.example.bankaccount.repository.BankAccountRepository;
import com.example.bankaccount.rules.BusinessRule;
import com.example.bankaccount.rules.CheckingAccountWithdrawalRule;
import com.example.bankaccount.rules.DepositRule;
import com.example.bankaccount.rules.SavingsAccountWithdrawalRule;
import com.example.bankaccount.service.CustomerValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BankAccountServiceImplTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private CustomerValidationService customerValidationService;

    @Mock
    private DepositRule depositRule;

    @Mock
    private SavingsAccountWithdrawalRule savingsRule;

    @Mock
    private CheckingAccountWithdrawalRule checkingRule;

    @InjectMocks
    private BankAccountServiceImpl bankAccountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Inicializar el mapa de reglas en BankAccountServiceImpl
        bankAccountService = new BankAccountServiceImpl(
                bankAccountRepository,
                customerValidationService
        );
    }

    @Test
    void testCreateBankAccount_CustomerExists() {
        BankAccount account = new BankAccount();
        account.setCustomerId(1L);

        when(customerValidationService.isCustomerExists(1L)).thenReturn(true);
        when(bankAccountRepository.save(account)).thenReturn(account);

        BankAccount result = bankAccountService.createBankAccount(account);

        assertNotNull(result);
        verify(customerValidationService, times(1)).isCustomerExists(1L);
        verify(bankAccountRepository, times(1)).save(account);
    }

    @Test
    void testCreateBankAccount_CustomerDoesNotExist() {
        BankAccount account = new BankAccount();
        account.setCustomerId(1L);

        when(customerValidationService.isCustomerExists(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> bankAccountService.createBankAccount(account));
        verify(customerValidationService, times(1)).isCustomerExists(1L);
        verify(bankAccountRepository, never()).save(account);
    }

    @Test
    void testGetAllBankAccounts() {
        List<BankAccount> accounts = List.of(new BankAccount(), new BankAccount());
        when(bankAccountRepository.findAll()).thenReturn(accounts);

        List<BankAccount> result = bankAccountService.getAllBankAccounts();

        assertEquals(2, result.size());
        verify(bankAccountRepository, times(1)).findAll();
    }

    @Test
    void testGetBankAccountById_AccountExists() {
        BankAccount account = new BankAccount();
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(account));

        Optional<BankAccount> result = bankAccountService.getBankAccountById(1L);

        assertTrue(result.isPresent());
        verify(bankAccountRepository, times(1)).findById(1L);
    }

    @Test
    void testGetBankAccountById_AccountDoesNotExist() {
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<BankAccount> result = bankAccountService.getBankAccountById(1L);

        assertFalse(result.isPresent());
        verify(bankAccountRepository, times(1)).findById(1L);
    }

    @Test
    void testDeposit_ValidAmount() {
        BankAccount account = new BankAccount();
        account.setBalance(BigDecimal.valueOf(100));

        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(bankAccountRepository.save(any())).thenReturn(account);

        BankAccount result = bankAccountService.deposit(1L, BigDecimal.valueOf(50));

        assertEquals(BigDecimal.valueOf(150), result.getBalance());
        verify(bankAccountRepository, times(1)).save(account);
    }

    @Test
    void testDeposit_InvalidAmount() {
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> bankAccountService.deposit(1L, BigDecimal.valueOf(50)));
        verify(bankAccountRepository, never()).save(any());
    }

    @Test
    void testWithdraw_ValidSavingsAccount() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        BankAccount account = new BankAccount();
        account.setId(1L); // Configura el ID correctamente
        account.setAccountType(BankAccount.AccountType.SAVINGS);
        account.setBalance(BigDecimal.valueOf(100));

        // Mock del repositorio
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(account));

        // Mock de la regla de retiro
        doNothing().when(savingsRule).validate(account, BigDecimal.valueOf(50));

        //mock del save
        when(bankAccountRepository.save(any())).thenReturn(account);

        // Usar reflection para sobrescribir el mapa de reglas
        Field withdrawalRulesField = BankAccountServiceImpl.class.getDeclaredField("withdrawalRules");
        withdrawalRulesField.setAccessible(true);

        Map<BankAccount.AccountType, BusinessRule> mockRules = Map.of(
                BankAccount.AccountType.SAVINGS, savingsRule,
                BankAccount.AccountType.CHECKING, checkingRule
        );

        withdrawalRulesField.set(bankAccountService, mockRules);

        // Act
        BankAccount result = bankAccountService.withdraw(1L, BigDecimal.valueOf(50));

        // Assert
        assertEquals(BigDecimal.valueOf(50), result.getBalance());
        verify(savingsRule, times(1)).validate(account, BigDecimal.valueOf(50));
        verify(bankAccountRepository, times(1)).save(account);
    }

    @Test
    void testSetAccountType_NullValueThrowsException() {
        BankAccount account = new BankAccount();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> account.setAccountType(null));
        assertEquals("Account type is required.", exception.getMessage());
    }


    @Test
    void testDeleteBankAccount_AccountExists() {
        BankAccount account = new BankAccount();
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(account));

        boolean result = bankAccountService.deleteBankAccount(1L);

        assertTrue(result);
        verify(bankAccountRepository, times(1)).delete(account);
    }

    @Test
    void testDeleteBankAccount_AccountDoesNotExist() {
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = bankAccountService.deleteBankAccount(1L);

        assertFalse(result);
        verify(bankAccountRepository, never()).delete(any());
    }
}
