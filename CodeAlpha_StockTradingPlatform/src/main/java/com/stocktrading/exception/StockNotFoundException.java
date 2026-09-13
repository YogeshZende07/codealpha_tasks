package com.stocktrading.exception;

/**
 * Thrown when a requested stock symbol does not exist in the market.
 */
public class StockNotFoundException extends Exception {
    public StockNotFoundException(String message) {
        super(message);
    }
}
