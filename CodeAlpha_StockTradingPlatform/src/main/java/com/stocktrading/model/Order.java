package com.stocktrading.model;

/**
 * Represents an intent to trade before it is executed and turned into a Transaction.
 * Kept lightweight and used internally by TradingService for validation.
 */
public class Order {

    private final int userId;
    private final String symbol;
    private final int quantity;
    private final TransactionType type;

    public Order(int userId, String symbol, int quantity, TransactionType type) {
        this.userId = userId;
        this.symbol = symbol;
        this.quantity = quantity;
        this.type = type;
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

    public TransactionType getType() {
        return type;
    }
}
