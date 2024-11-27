package com.example.bankaccount.client;

import com.example.bankaccount.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Service to interact with the Customer microservice.
 * Provides methods to check the existence of a customer.
 */
@Service
public class CustomerClient {

    private final WebClient webClient;

    /**
     * Constructor to initialize the CustomerClient with a WebClient instance.
     *
     * @param webClient The WebClient instance to communicate with the Customer microservice.
     */
    @Autowired
    public CustomerClient(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Checks if a customer exists in the Customer microservice.
     *
     * @param customerId The ID of the customer to verify.
     * @return true if the customer exists; false otherwise.
     */
    public boolean isCustomerExists(Long customerId) {
        try {
            // Sends a GET request to the Customer microservice to check if the customer exists
            return webClient.get()
                    .uri("/customers/{id}", customerId) // Replace {id} with the actual customerId
                    .retrieve()
                    .bodyToMono(Customer.class) // Maps the response to a Customer object
                    .blockOptional() // Blocks and retrieves the response as an Optional
                    .isPresent(); // Returns true if the customer is found
        } catch (Exception e) {
            // In case of an error (e.g., customer not found or communication failure), assume the customer does not exist
            return false;
        }
    }
}
