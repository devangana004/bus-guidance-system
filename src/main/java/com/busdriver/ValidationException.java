package com.busdriver;

/**
 * Custom exception thrown when a validation rule is broken.
 * For example: invalid busID, capacity increase attempt, etc.
 */
public class ValidationException extends Exception {

    public ValidationException(String message) {
        super(message);
    }
}