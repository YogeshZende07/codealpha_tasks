package com.stocktrading.exception;

/**
 * Thrown when a quantity supplied for a buy/sell order is not valid (e.g. zero or negative).
 */
public class InvalidQuantityException extends Exception {
    public InvalidQuantityException(String message) {
        super(message);
    }
}
