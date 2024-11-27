package com.example.bankaccount.service.impl;

import com.example.bankaccount.client.CustomerClient;
import com.example.bankaccount.service.CustomerValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

class CustomerValidationServiceImplTest {

    @Mock
    private CustomerClient customerClient;

    @InjectMocks
    private CustomerValidationServiceImpl customerValidationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa los mocks
    }

    @Test
    void testIsCustomerExists_ReturnsTrue_WhenCustomerExists() {
        // Arrange
        Long customerId = 1L;
        when(customerClient.isCustomerExists(customerId)).thenReturn(true);

        // Act
        boolean result = customerValidationService.isCustomerExists(customerId);

        // Assert
        assertTrue(result, "Expected customer to exist.");
    }

    @Test
    void testIsCustomerExists_ReturnsFalse_WhenCustomerDoesNotExist() {
        // Arrange
        Long customerId = 2L;
        when(customerClient.isCustomerExists(customerId)).thenReturn(false);

        // Act
        boolean result = customerValidationService.isCustomerExists(customerId);

        // Assert
        assertFalse(result, "Expected customer to not exist.");
    }
}
