package com.stocktrading.model;

import java.time.LocalDateTime;

/**
 * Abstract base class for a trading transaction.
 * Demonstrates abstraction and inheritance: BuyTransaction and SellTransaction
 * both extend this class and provide their own description behaviour (polymorphism).
 */
public abstract class Transaction {

    private final int id;
    private final int userId;
    private final String symbol;
    private final int quantity;
    private final double price;
    private final LocalDateTime timestamp;

    protected Transaction(int id, int userId, String symbol, int quantity, double price, LocalDateTime timestamp) {
        this.id = id;
        this.userId = userId;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public double getTotalAmount() {
        return quantity * price;
    }

    /**
     * Each concrete subclass reports its own transaction type (polymorphism).
     */
    public abstract TransactionType getType();
}
