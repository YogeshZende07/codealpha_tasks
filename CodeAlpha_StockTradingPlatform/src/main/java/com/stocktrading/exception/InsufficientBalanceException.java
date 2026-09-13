package com.stocktrading.exception;

/**
 * Thrown when a user attempts to buy stock without enough cash balance.
 */
public class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
