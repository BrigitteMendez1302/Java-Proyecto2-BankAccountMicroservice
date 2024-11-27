package com.example.bankaccount.service.impl;

import com.example.bankaccount.client.CustomerClient;
import com.example.bankaccount.service.CustomerValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerValidationServiceImpl implements CustomerValidationService {

    private final CustomerClient customerClient;

    @Autowired
    public CustomerValidationServiceImpl(CustomerClient customerClient) {
        this.customerClient = customerClient;
    }

    @Override
    public boolean isCustomerExists(Long customerId) {
        return customerClient.isCustomerExists(customerId);
    }
}
