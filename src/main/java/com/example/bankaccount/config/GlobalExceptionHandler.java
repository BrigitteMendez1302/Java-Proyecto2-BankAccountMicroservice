package com.example.bankaccount.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Global exception handler for managing application-specific exceptions.
 * Provides consistent error responses for various types of exceptions.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation errors triggered by invalid method arguments.
     *
     * @param ex The MethodArgumentNotValidException thrown during validation.
     * @return A ResponseEntity containing a map of field-specific error messages with HTTP status 400.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        // Collect field-specific validation error messages
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField(); // Extract the field name causing the error
            String errorMessage = error.getDefaultMessage(); // Get the associated error message
            errors.put(fieldName, errorMessage); // Add the field and message to the error map
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST); // Return with HTTP 400 status
    }

    /**
     * Handles IllegalArgumentException, typically caused by invalid method arguments.
     *
     * @param ex The IllegalArgumentException thrown.
     * @return A ResponseEntity with the error message and HTTP status 400.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage()); // Add the exception message
        errorResponse.put("status", "400"); // Indicate the HTTP status
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // Return with HTTP 400 status
    }

    /**
     * Handles DataIntegrityViolationException, typically caused by database constraint violations.
     *
     * @param ex The DataIntegrityViolationException thrown.
     * @return A ResponseEntity with a detailed error message and HTTP status 409.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Data integrity violation: " + ex.getMessage()); // Add detailed error message
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT); // Return with HTTP 409 status
    }

    /**
     * Handles NoSuchElementException, typically caused when an expected element is not found.
     *
     * @param ex The NoSuchElementException thrown.
     * @return A ResponseEntity with the error message and HTTP status 404.
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNoSuchElementException(NoSuchElementException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage()); // Add the exception message
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND); // Return with HTTP 404 status
    }
}
