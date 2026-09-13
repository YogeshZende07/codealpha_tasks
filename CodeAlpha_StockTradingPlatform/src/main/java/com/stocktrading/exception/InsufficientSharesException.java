package com.stocktrading.exception;

/**
 * Thrown when a user attempts to sell more shares than they currently hold.
 */
public class InsufficientSharesException extends Exception {
    public InsufficientSharesException(String message) {
        super(message);
    }
}
